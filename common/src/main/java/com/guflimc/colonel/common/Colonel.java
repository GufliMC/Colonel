package com.guflimc.colonel.common;

import com.guflimc.colonel.common.annotation.Command;
import com.guflimc.colonel.common.annotation.parameter.Source;
import com.guflimc.colonel.common.command.CommandDispatcher;
import com.guflimc.colonel.common.command.CommandDispatcherContext;
import com.guflimc.colonel.common.command.builder.CommandSyntaxBuilder;
import com.guflimc.colonel.common.command.syntax.CommandExecutor;
import com.guflimc.colonel.common.command.syntax.CommandParameter;
import com.guflimc.colonel.common.command.syntax.CommandParameterParser;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Colonel {

    private final CommandDispatcher dispatcher = new CommandDispatcher();

    public CommandDispatcher dispatcher() {
        return dispatcher;
    }

    public CommandDispatcherContext context() {
        return dispatcher.context();
    }

    //

    public void register(@NotNull Object container) {

    }

    private void registerCommands(@NotNull Object container, @NotNull Method method) {
        Command[] commands = method.getAnnotationsByType(Command.class);
        for (Command cmd : commands) {
            if (cmd == null || cmd.value().trim().length() == 0) {
                throw new IllegalArgumentException(String.format("Command annotation is empty for method '%s' in %s.",
                        method.getName(), container.getClass().getSimpleName()));
            }
        }

        List<CommandParameter<?>> commandParameters = new ArrayList<>();

        Map<Parameter, CommandParameterParser<?>> parameters = new HashMap<>();
        Parameter[] params = method.getParameters();
        for (Parameter parameter : params) {
            // source
            Source source = parameter.getAnnotation(Source.class);
            if ( source != null) {
                continue;
            }

            // parameter
            String name = parameter.getName();
            String type = null;
            com.guflimc.colonel.common.annotation.parameter.Parameter paramconf = parameter.getAnnotation(com.guflimc.colonel.common.annotation.parameter.Parameter.class);
            if ( paramconf != null ) {
                if ( !paramconf.name().equals(com.guflimc.colonel.common.annotation.parameter.Parameter.NAME_INFERRED) ) {
                    name = paramconf.name();
                }
                if ( !paramconf.type().equals(com.guflimc.colonel.common.annotation.parameter.Parameter.NAME_INFERRED) ) {
                    type = paramconf.type();
                }
            }

            if ( type != null ) {
                builder.withParameter(name, parameter.getType(), type);
            } else {
                builder.withParameter(name, parameter.getType());
            }
        }

        CommandExecutor executor = context -> {
            Object[] args = new Object[params.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = parameters.get(params[i]).parse(context);
            }
            try {
                method.invoke(container, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };

    }


}
