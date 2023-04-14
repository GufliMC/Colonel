package com.guflimc.colonel.common;

import com.guflimc.colonel.common.annotation.Command;
import com.guflimc.colonel.common.annotation.parameter.Parameter;
import com.guflimc.colonel.common.annotation.parameter.Source;
import com.guflimc.colonel.common.command.CommandContext;
import com.guflimc.colonel.common.command.CommandDispatcher;
import com.guflimc.colonel.common.registry.TypeRegistryContainer;
import com.guflimc.colonel.common.command.syntax.CommandParameterType;
import com.guflimc.colonel.common.builder.CommandMethod;
import com.guflimc.colonel.common.builder.CommandMethodParameter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Colonel<S> {

    private final static String SPACE = Pattern.quote(" ");

    //

    private final CommandDispatcher dispatcher = new CommandDispatcher();
    private final TypeRegistryContainer registry = new TypeRegistryContainer();

    public CommandDispatcher dispatcher() {
        return dispatcher;
    }

    public TypeRegistryContainer registry() {
        return registry;
    }

    //

    protected void enhance(@NotNull CommandMethod<S> cm, @NotNull Method method) {
    }

    protected void enhance(@NotNull CommandMethodParameter<?> cmp, @NotNull java.lang.reflect.Parameter parameter) {
    }

    //

    public final void register(@NotNull Object container) {
        Class<?> cc = container.getClass();
        for (Method method : cc.getDeclaredMethods()) {
            if (method.getAnnotationsByType(Command.class).length == 0) {
                continue;
            }

            CommandMethod<S> cm = parse(method, container);
            Arrays.stream(cm.build()).forEach(syn -> dispatcher.register(syn, cm.executor()));
        }
    }

    private CommandMethod<S> parse(@NotNull Method method, @NotNull Object container) {
        Command[] commands = method.getAnnotationsByType(Command.class);
        for (Command cmd : commands) {
            if (cmd == null || cmd.value().trim().length() == 0) {
                throw new IllegalArgumentException(String.format("Command annotation is empty for method '%s'.",
                        method.getName()));
            }
        }

        List<CommandMethodParameter<?>> parameters = new ArrayList<>();
        Map<java.lang.reflect.Parameter, Function<CommandContext, Object>> mappers = new HashMap<>();

        java.lang.reflect.Parameter[] params = method.getParameters();
        for (java.lang.reflect.Parameter parameter : params) {
            // source
            Source source = parameter.getAnnotation(Source.class);
            if ( source != null) {
                if ( !source.value().equals(Source.NAME_INFERRED) ) {
                    mappers.put(parameter, ctx -> {
                        if ( parameter.getType().isInstance(ctx.source()) )
                            return ctx.source();
                        return registry.sourceType(parameter.getType(), source.value()).value(ctx);
                    });
                } else {
                    mappers.put(parameter, ctx -> {
                        if ( parameter.getType().isInstance(ctx.source()) )
                            return ctx.source();
                        return registry.sourceType(parameter.getType()).value(ctx);
                    });
                }
                continue;
            }

            // parameter
            CommandMethodParameter<?> param = parse(parameter);
            enhance(param, parameter);

            parameters.add(param);
            mappers.put(parameter, context -> context.argument(param.name()));
        }

        String[][] literals = Arrays.stream(commands).map(c -> c.value().split(SPACE)).toArray(String[][]::new);
        CommandMethod<S> cm = new CommandMethod<>(literals, parameters.toArray(CommandMethodParameter[]::new));

        // create executor
        cm.setExecutor(context -> {
            Object[] args = new Object[params.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = mappers.get(params[i]).apply(context);
            }
            try {
                method.invoke(container, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });

        enhance(cm, method);
        return cm;
    }

    private CommandMethodParameter<?> parse(@NotNull java.lang.reflect.Parameter parameter) {
        String name = parameter.getName();
        String type = null;
        Parameter paramconf = parameter.getAnnotation(Parameter.class);
        if ( paramconf != null ) {
            if ( !paramconf.value().equals(Parameter.NAME_INFERRED) ) {
                name = paramconf.value();
            }
            if ( !paramconf.type().equals(Parameter.NAME_INFERRED) ) {
                type = paramconf.type();
            }
        }

        CommandParameterType<?> parameterType;
        if ( type != null ) {
            parameterType = registry().parameterType(parameter.getType(), type);
        } else {
            parameterType = registry().parameterType(parameter.getType());
        }

        return new CommandMethodParameter<>(name, parameterType);
    }




}
