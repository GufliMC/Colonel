package com.guflimc.colonel.common;

import com.guflimc.colonel.common.annotation.*;
import com.guflimc.colonel.common.exception.ColonelRegistrationFailedException;
import com.guflimc.colonel.common.registry.ArgumentMapperRegistry;
import com.guflimc.colonel.common.registry.SourceMapperRegistry;
import com.guflimc.colonel.common.registry.SuggestionProviderRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

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

    private final CommandDispatcher<S> dispatcher;

    private final ArgumentMapperRegistry argumentMapperRegistry = new ArgumentMapperRegistry();
    private final SourceMapperRegistry<S> sourceMapperRegistry = new SourceMapperRegistry<>();
    private final SuggestionProviderRegistry<S> suggestionProviderRegistry = new SuggestionProviderRegistry<>();

    private final Collection<CommandNode<S>> nodes = new ArrayList<>();
    private BiPredicate<S, String> permissionValidator = null; // defaults to ignore permission annotations

    public Colonel(CommandDispatcher<S> dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Colonel() {
        this(new CommandDispatcher<>());
    }

    //

    /**
     * @return the {@link CommandDispatcher} used by this {@link Colonel}.
     */
    public CommandDispatcher<S> dispatcher() {
        return dispatcher;
    }

    /**
     * @return the {@link ArgumentMapperRegistry} used by this {@link Colonel}.
     */
    public ArgumentMapperRegistry argumentMapperRegistry() {
        return argumentMapperRegistry;
    }

    /**
     * @return the {@link SourceMapperRegistry} used by this {@link Colonel}.
     */
    public SourceMapperRegistry<S> sourceMapperRegistry() {
        return sourceMapperRegistry;
    }

    /**
     * @return the {@link SuggestionProviderRegistry} used by this {@link Colonel}.
     */
    public SuggestionProviderRegistry<S> suggestionProviderRegistry() {
        return suggestionProviderRegistry;
    }

    /**
     * Set a {@link BiPredicate} that will be used to validate permissions of a command source.
     *
     * @param permissionValidator the {@link BiPredicate} to use.
     */
    public void setPermissionValidator(@NotNull BiPredicate<S, String> permissionValidator) {
        this.permissionValidator = permissionValidator;
    }

    /**
     * Unregisters all commands registered by this {@link Colonel}.
     */
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

    /**
     * Registers all commands annotated with {@link Command} in the given object.
     *
     * @param container the object to register commands from.
     */
    public void registerCommands(@NotNull Object container) {
        Class<?> cc = container.getClass();
        for (Method method : cc.getDeclaredMethods()) {
            if (method.getAnnotation(CommandArgumentSuggestionProvider.class) == null) {
                continue;
            }

            // TODO register suggestion providers
        }

        // PARSE COMMANDS
        for (Method method : cc.getDeclaredMethods()) {
            if (method.getAnnotationsByType(Command.class).length == 0) {
                continue;
            }

            registerCommands(container, method);
        }
    }

    private void registerCommands(@NotNull Object container, @NotNull Method method) {
        // SETUP PARAMETERS
        Map<Parameter, Parser<S, ?>> parsers = new HashMap<>();

        // SETUP COMMAND SOURCE
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(CommandSource.class) == null) {
                continue;
            }

            parsers.put(parameter, ctx -> sourceMapperRegistry
                    .map(ctx, parameter.getType()).orElse(ctx.getSource()));
        }

        // SETUP ARGUMENTS
        List<ArgumentBuilder<S, ?>> arguments = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.getAnnotation(CommandSource.class) != null) {
                continue;
            }

            ArgumentType<?> type = argumentMapperRegistry.map(parameter).orElse(null);
            if (type == null) {
                throw new ColonelRegistrationFailedException(String
                        .format("Argument type not found for %s in %s.", parameter.getName(), method.getName()));
            }

            // type parser
            parsers.put(parameter, ctx -> ctx.getArgument(parameter.getName(), parameter.getType()));

            // create argument
            RequiredArgumentBuilder<S, ?> argument = argument(parameter.getName(), type);
            arguments.add(argument);

            // suggestions
            String suggestionProviderName = null;
            CommandArgumentSuggestions suggestions = parameter.getAnnotation(CommandArgumentSuggestions.class);
            if ( suggestions != null ) {
                suggestionProviderName = suggestions.value();
            }

            suggestionProviderRegistry.provider(suggestionProviderName, parameter.getType())
                    .ifPresent(argument::suggests);
        }

        // SETUP EXECUTOR
        com.mojang.brigadier.Command<S> command = ctx -> {
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = parsers.get(parameters[i]).parse(ctx);
            }
            try {
                method.invoke(container, args);
                return 1;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ColonelRegistrationFailedException(e);
            }
        };

        // PERMISSIONS
        CommandPermissions permissions = method.getAnnotation(CommandPermissions.class);
        Predicate<S> requires = null;
        if (permissions != null && permissionValidator != null ) {
            requires = permissionsPredicate(permissions);
        }

        // BUILD TREE
        Command[] commands = method.getAnnotationsByType(Command.class);
        for (Command cmd : commands) {
            if (cmd == null || cmd.value().trim().length() == 0) {
                throw new ColonelRegistrationFailedException(String
                        .format("Command annotation is empty for %s.", method.getName()));
            }

            List<ArgumentBuilder<S, ?>> parts = new ArrayList<>();

            // literals
            String[] literals = cmd.value().trim().split(Pattern.quote(" "));
            for (String literal : literals) {
                parts.add(literal(literal));
            }

            // arguments
            parts.addAll(arguments);

            // executor
            ArgumentBuilder<S, ?> exitNode = parts.get(parts.size() - 1);
            exitNode.executes(command);

            // requires (permissions)
            if (requires != null) {
                exitNode.requires(requires);
            }

            // build actual tree
            for (int i = parts.size() - 1; i > 0; i--) {
                parts.get(i - 1).then(parts.get(i));
            }

            // register
            CommandNode<S> node = parts.get(0).build();
            dispatcher.getRoot().addChild(node);
            nodes.add(node);
        }
    }

    private Predicate<S> permissionsPredicate(CommandPermissions permissions) {
        return source -> {
            int match = (int) Arrays.stream(permissions.value()).filter(permissionTest(source)).count();
            return permissions.gate().test(match, permissions.value().length);
        };
    }

    private Predicate<CommandPermissions.CommandPermission> permissionTest(S source) {
        return permission -> {
            if (permission.negate()) {
                return !permissionValidator.test(source, permission.value());
            }
            return permissionValidator.test(source, permission.value());
        };
    }

    @FunctionalInterface
    private interface Parser<S, T> {
        T parse(CommandContext<S> ctx);
    }

}
