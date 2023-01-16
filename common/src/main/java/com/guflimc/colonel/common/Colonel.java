package com.guflimc.colonel.common;

import com.guflimc.colonel.common.annotation.*;
import com.guflimc.colonel.common.builder.AnnotatedArgumentBuilder;
import com.guflimc.colonel.common.builder.AnnotatedCommandBuilder;
import com.guflimc.colonel.common.exception.ColonelRegistrationFailedException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.CommandNode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Colonel<S> {

    private final static Field[] CHILDREN_FIELDS;

    static {
        try {
            Field CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            Field LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            Field ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            CHILDREN_FIELDS = new Field[]{CHILDREN_FIELD, LITERALS_FIELD, ARGUMENTS_FIELD};
            for (Field field : CHILDREN_FIELDS) {
                field.setAccessible(true);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    //

    // dispatcher, provided in constructor
    private final CommandDispatcher<S> dispatcher;

    // list of 2nd-level nodes that are registered by colonel.
    // TODO this should only be exit nodes.
    private final Collection<CommandNode<S>> nodes = new ArrayList<>();

    // registries
    private final ColonelConfig<S> config = new ColonelConfig<>();


    public Colonel(CommandDispatcher<S> dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Colonel() {
        this(new CommandDispatcher<>());
    }

    //

    public CommandDispatcher<S> dispatcher() {
        return dispatcher;
    }

    public ColonelConfig<S> config() {
        return config;
    }

    //

    public void unregisterAll() {
        nodes.forEach(this::unregister);
    }

    private void unregister(@NotNull CommandNode<S> node) {
        try {
            for (Field field : CHILDREN_FIELDS) {
                Map<String, ?> children = (Map<String, ?>) field.get(dispatcher.getRoot());
                children.remove(node.getName());
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    //

    public void registerCommands(@NotNull Object container) {
        Class<?> cc = container.getClass();
        for (Method method : cc.getDeclaredMethods()) {
            if (method.getAnnotation(SuggestionProvider.class) == null) {
                continue;
            }

            registerSuggestions(container, method);
        }

        // PARSE COMMANDS
        for (Method method : cc.getDeclaredMethods()) {
            if (method.getAnnotationsByType(Command.class).length == 0) {
                continue;
            }

            registerCommands(container, method);
        }
    }

    private void registerSuggestions(@NotNull Object container, @NotNull Method method) {
        if (!List.class.isAssignableFrom(method.getReturnType())) {
            throw new ColonelRegistrationFailedException(String.format("The return type of %s must be a List.", method.getName()));
        }
        if (method.getParameters().length == 1 && !method.getParameters()[0].getType().equals(CommandContext.class)) {
            throw new ColonelRegistrationFailedException(String.format("%s must have a single parameter of type CommandContext.", method.getName()));
        }

        SuggestionProvider annotation = method.getAnnotation(SuggestionProvider.class);
        if (annotation.target() == null) {
            throw new ColonelRegistrationFailedException(String.format("The target class for %s must be specified.", method.getName()));
        }

        String name = annotation.value().trim();
        if (name.isEmpty()) name = null;

        com.mojang.brigadier.suggestion.SuggestionProvider<S> provider = (ctx, b) -> {
            List<?> result;
            try {
                result = (List<?>) method.invoke(container, ctx);
                Objects.requireNonNull(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (result.get(0) instanceof Suggestion) {
                return CompletableFuture.completedFuture(com.mojang.brigadier.suggestion.Suggestions.create(ctx.getInput(), (List<Suggestion>) result));
            }

            result.stream().map(Object::toString).forEach(b::suggest);

            return CompletableFuture.completedFuture(b.build());
        };

        config.withSuggestionSupplier(name, annotation.target(), provider);
    }

    private void registerCommands(@NotNull Object container, @NotNull Method method) {
        AnnotatedCommandBuilder<S> builder = new AnnotatedCommandBuilder<>(config);

        // literals
        Command[] commands = method.getAnnotationsByType(Command.class);
        for (Command cmd : commands) {
            if (cmd == null || cmd.value().trim().length() == 0) {
                throw new ColonelRegistrationFailedException(String
                        .format("Command annotation is empty for %s.", method.getName()));
            }
            builder.withLiterals(cmd.value());
        }

        // permissions
        Permission[] permissions = method.getAnnotationsByType(Permission.class);
        if (permissions != null) {
            builder.withPermissions(permissions);
        }


        PermissionsLogic permissionsLogic = method.getAnnotation(PermissionsLogic.class);
        if ( permissionsLogic != null ) {
            builder.withPermissionsLogic(permissionsLogic);
        }

        // parameters
        Map<Parameter, ParameterMapper<S, ?>> parameters = new HashMap<>();

        // arguments & command source
        Parameter[] params = method.getParameters();
        for (Parameter parameter : params) {

            // command source
            if (parameter.getAnnotation(CommandSource.class) != null) {
                parameters.put(parameter, ctx -> config.commandSource(ctx, parameter.getType()).orElse(ctx.getSource()));
                continue;
            }

            // arguments
            builder.withArgument(parameter.getName(), b -> argument(parameter, b));
            parameters.put(parameter, ctx -> ctx.getArgument(parameter.getName(), parameter.getType()));
        }

        // executor
        builder.withExecutor(ctx -> {
            Object[] args = new Object[params.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = parameters.get(params[i]).parse(ctx);
            }
            try {
                method.invoke(container, args);
                return com.mojang.brigadier.Command.SINGLE_SUCCESS;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ColonelRegistrationFailedException(e);
            }
        });

        // register
        builder.register(dispatcher.getRoot());
    }

    private <T> void argument(@NotNull Parameter parameter, @NotNull AnnotatedArgumentBuilder<S, T> b) {
        ArgumentType<?> type = config.argumentType(parameter).orElse(null);
        if (type == null) {
            throw new ColonelRegistrationFailedException(String
                    .format("Argument type not found for %s with type %s.", parameter.getName(), parameter.getType().getSimpleName()));
        }

        b.withArgumentType((ArgumentType<T>) type);

        // suggestions
        String name = null;

        Suggestions suggestions = parameter.getAnnotation(Suggestions.class);
        if (suggestions != null && !suggestions.value().trim().isEmpty()) {
            name = suggestions.value().trim();
            if ( name.equals(Suggestions.TYPE_OR_NAME_INFERRED) ) {
                name = parameter.getName();
            }
        }

        com.mojang.brigadier.suggestion.SuggestionProvider<S> provider = config.suggestionSupplier(name, parameter.getType()).orElse(null);
        if (provider != null) {
            b.withSuggestions(provider);
            return;
        }

        if (!parameter.getType().isEnum()) {
            return;
        }

        // fallback to automatic enum suggestions
        b.withSuggestions((ctx, sb) -> {
            Arrays.stream(parameter.getType().getEnumConstants()).forEach(o -> sb.suggest(o.toString()));
            return CompletableFuture.completedFuture(sb.build());
        });
    }

    @FunctionalInterface
    private interface ParameterMapper<S, T> {
        T parse(CommandContext<S> ctx);
    }

}
