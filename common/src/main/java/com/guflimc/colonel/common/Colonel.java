package com.guflimc.colonel.common;

import com.guflimc.colonel.common.annotations.Command;
import com.guflimc.colonel.common.annotations.CommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
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

    private final Collection<CommandNode<S>> nodes = new ArrayList<>();

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

    public void register(@NotNull Object container) {
        Class<?> cc = container.getClass();
        for (Method method : cc.getDeclaredMethods()) {
            if (method.getAnnotationsByType(Command.class).length == 0) {
                continue;
            }

            register(container, method);
        }
    }

    public void unregisterAll() {
        nodes.forEach(this::unregister);
    }

    private void unregister(CommandNode<S> node) {
        try {
            for (Field field : CHILDREN_FIELDS) {
                Map<String, ?> children = (Map<String, ?>) field.get(dispatcher.getRoot());
                children.remove(node.getName());
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(@NotNull Object container, @NotNull Method method) {
        // SETUP PARAMETERS
        Map<Parameter, Parser<S, ?>> parsers = new HashMap<>();

        // SETUP COMMAND SOURCE
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(CommandSource.class) == null) {
                continue;
            }

            // TODO derived types for command source
            parsers.put(parameter, CommandContext::getSource);
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
                // TODO error message
                System.out.println("Argument type not found for " + parameter.getName() + " in " + method.getName());
                return;
            }

            // TODO argument suggestions

            parsers.put(parameter, ctx -> ctx.getArgument(parameter.getName(), parameter.getType()));
            arguments.add(argument(parameter.getName(), type));
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
                throw new RuntimeException(e);
            }
        };

        // TODO argument perimissions

        // BUILD TREE
        Command[] commands = method.getAnnotationsByType(Command.class);
        for (Command cmd : commands) {
            if (cmd == null || cmd.value().trim().length() == 0) {
                // TODO error message
                System.out.println("Command annotation is empty for " + method.getName());
                return;
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
            parts.get(parts.size() - 1).executes(command);

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

    @FunctionalInterface
    private interface Parser<S, T> {
        T parse(CommandContext<S> ctx);
    }

}
