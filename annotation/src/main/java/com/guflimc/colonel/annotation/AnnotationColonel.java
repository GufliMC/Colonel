package com.guflimc.colonel.annotation;

import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.build.*;
import com.guflimc.colonel.common.dispatch.definition.CommandParameter;
import com.guflimc.colonel.common.dispatch.definition.ReadMode;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.dispatch.tree.CommandHandler;
import com.guflimc.colonel.common.ext.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnnotationColonel<S> extends Colonel<S> {

    protected final Class<S> sourceType;

    public AnnotationColonel(Class<S> sourceType) {
        this.sourceType = sourceType;
    }

    //

    public void registerAll(@NotNull Object container) {
        Class<?> cc = container.getClass();

        // utility methods first
        for (Method method : cc.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Completer.class)) {
                registerCompleter(method, container);
                continue;
            }

            if (method.isAnnotationPresent(Parser.class)) {
                registerParser(method, container);
                continue;
            }
        }

        // then command methods
        for (Method method : cc.getDeclaredMethods()) {
            if (method.getAnnotationsByType(Command.class).length > 0) {
                registerCommands(method, container);
            }
        }
    }

    private void registerCommands(@NotNull Method method, @NotNull Object container) {
        Map<Parameter, Function<CommandContext<S>, Object>> suppliers = new LinkedHashMap<>();

        CommandHandlerBuilder<S> builder = builder();
        build(method, suppliers, builder);

        // set executor
        builder.executor(ctx -> {
            Object[] arguments = suppliers.values().stream()
                    .map(f -> f.apply(ctx))
                    .toArray();

            try {
                method.invoke(container, arguments);
            }  catch (IllegalArgumentException e) {
                throw new RuntimeException(invocationErrorMessage(method, arguments), e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // register
        builder.register();
    }

    protected void build(@NotNull Method method, @NotNull Map<Parameter, Function<CommandContext<S>, Object>> suppliers, @NotNull CommandHandlerBuilder<S> builder) {
        method.setAccessible(true);

        Command[] commands = method.getAnnotationsByType(Command.class);
        for (Command cmd : commands) {
            if (cmd == null || cmd.value().trim().length() == 0) {
                throw new IllegalArgumentException(String.format("Command annotation is empty for method '%s' in class '%s'.",
                        method.getName(), method.getDeclaringClass().getSimpleName()));
            }

            // path
            builder.path(cmd.value());
        }

        for (Parameter param : method.getParameters()) {
            // SOURCE
            if (param.isAnnotationPresent(Source.class)) {
                CommandSourceMapper<S> mapper = sourceMapper(param);
                suppliers.put(param, ctx -> mapper.map(ctx.source()));
                continue;
            }

            // PARAMETER
            try {
                CommandParameterBuilder<S> pb = builder.parameter();
                build(param, suppliers, pb);
                pb.done();
            } catch (Exception e) {
                throw new RuntimeException(String.format("Error while building parameter '%s' in method '%s' in class '%s'.",
                        param.getName(),
                        method.getName(),
                        method.getDeclaringClass().getSimpleName()), e);
            }
        }
    }

    protected void build(@NotNull Parameter parameter, @NotNull Map<Parameter, Function<CommandContext<S>, Object>> suppliers, int index) {

    }
    
    protected void build(@NotNull Parameter parameter, @NotNull Map<Parameter, Function<CommandContext<S>, Object>> suppliers, @NotNull CommandParameterBuilder<S> builder) {
        // PARAMETER
        com.guflimc.colonel.annotation.annotations.parameter.Parameter paramConf =
                parameter.getAnnotation(com.guflimc.colonel.annotation.annotations.parameter.Parameter.class);

        // name
        String name;
        if (paramConf != null && !paramConf.value().isEmpty()) {
            name = paramConf.value();
        } else {
            name = parameter.getName();
        }
        builder.name(name);

        // set supplier
        suppliers.put(parameter, ctx -> ctx.argument(name));

        // read mode
        ReadMode mode = ReadMode.STRING;
        if (paramConf != null) {
            builder.readMode(mode);
        }

        // parser
        if ( paramConf != null && !paramConf.parser().isEmpty() ) {
            builder.parser(parameter.getType(), paramConf.parser());
        } else {
            builder.parser(parameter.getType());
        }

        // completer
        if ( paramConf != null && !paramConf.completer().isEmpty() ) {
            builder.completer(parameter.getType(), paramConf.completer());
        } else {
            builder.completer(parameter.getType());
        }
    }

    //

    /**
     * Register a new parameter completer which maps to the given method in the given object.
     */
    private void registerCompleter(@NotNull Method method, @NotNull Object container) {
        method.setAccessible(true);

        if (!method.getReturnType().equals(List.class)) {
            throw new IllegalArgumentException(String.format("Completer method '%s' in class '%s' must return a List.",
                    method.getName(), container.getClass().getSimpleName()));
        }

        Map<Parameter, BiFunction<CommandContext<S>, String, Object>> suppliers = suppliers(method);

        Completer completerConf = method.getAnnotation(Completer.class);
        String name = method.getName();
        if (!completerConf.value().isEmpty()) {
            name = completerConf.value();
        }

        CommandParameterCompleter<S> completer = (context, input) -> {
            Object[] arguments = suppliers.values().stream()
                    .map(f -> f.apply(context, input))
                    .toArray();

            List<?> result;
            try {
                result = (List<?>) method.invoke(container, arguments);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(invocationErrorMessage(method, arguments), e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (result.isEmpty()) {
                return List.of();
            }

            return result.stream().map(o -> {
                if (o instanceof Suggestion s) {
                    return s;
                }
                return new Suggestion(o.toString());
            }).toList();
        };
        completer = CommandParameterCompleter.withMatchCheck(completer);

        if (completerConf.type() != Void.class) {
            registry().registerParameterCompleter(completerConf.type(), name, completer);
            return;
        }

        registry().registerParameterCompleter(Object.class, name, completer);
    }

    /**
     * Register a new parameter parser which maps to the given method in the given object.
     */
    private void registerParser(@NotNull Method method, @NotNull Object container) {
        method.setAccessible(true);

        if (method.getReturnType().equals(Void.TYPE)) {
            throw new IllegalArgumentException(String.format("Parser method '%s' in class '%s' must return something.",
                    method.getName(), container.getClass().getSimpleName()));
        }

        Map<Parameter, BiFunction<CommandContext<S>, String, Object>> suppliers = suppliers(method);

        Parser parserConf = method.getAnnotation(Parser.class);
        String name = method.getName();
        if (!parserConf.value().isEmpty()) {
            name = parserConf.value();
        }

        CommandParameterParser<S> parser = (context, input) -> {
            Object[] arguments = suppliers.values().stream()
                    .map(f -> f.apply(context, input))
                    .toArray();

            Object value;
            try {
                value = method.invoke(container, arguments);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(invocationErrorMessage(method, arguments), e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (value instanceof Argument arg) {
                return arg;
            }
            return Argument.success(value);
        };

        if ( parserConf.type() != Void.class ) {
            registry().registerParameterParser(parserConf.type(), name, parser);
            return;
        }

        if ( method.getReturnType() != Argument.class ) {
            registry().registerParameterParser(method.getReturnType(), name, parser);
            return;
        }

        throw new IllegalArgumentException(String.format("Parser method '%s' in class '%s' does not specify a type.",
                method.getName(), container.getClass().getSimpleName()));
    }

    /**
     * Creates a map which defines for each parameter, how to retrieve the correct value. This only works for utility
     * methods like parsers and completers.
     */
    private Map<Parameter, BiFunction<CommandContext<S>, String, Object>> suppliers(@NotNull Method method) {
        Map<Parameter, BiFunction<CommandContext<S>, String, Object>> suppliers = new LinkedHashMap<>();
        for (Parameter param : method.getParameters()) {

            // parameter is annotated
            if (param.isAnnotationPresent(Source.class)) {
                CommandSourceMapper<S> mapper = sourceMapper(param);
                suppliers.put(param, (ctx, input) -> mapper.map(ctx.source()));
                continue;
            }

            // default values
            if (param.getType().equals(CommandContext.class)) {
                suppliers.put(param, (ctx, input) -> ctx);
                continue;
            }
            if (param.getType().equals(String.class)) {
                suppliers.put(param, (ctx, input) -> input);
                continue;
            }
            throw new IllegalArgumentException(String.format("Utility method '%s' in class '%s' has an invalid parameter '%s' of type '%s'.",
                    method.getName(), method.getDeclaringClass().getSimpleName(), param.getName(), param.getType().getSimpleName()));
        }

        return suppliers;
    }

    /**
     * Returns a mapper for the given parameter based on it's {@link Source} annotation.
     */
    private CommandSourceMapper<S> sourceMapper(@NotNull Parameter param) {
        com.guflimc.colonel.annotation.annotations.parameter.Parameter paramConf = param
                .getAnnotation(com.guflimc.colonel.annotation.annotations.parameter.Parameter.class);

        // name
        String name = param.getName();
        if (paramConf != null && !paramConf.value().isEmpty()) {
            name = paramConf.value();
        }

        Source sourceConf = param.getAnnotation(Source.class);
        CommandSourceMapper<S> mapper;
        if ( !sourceConf.value().isEmpty() ) {
            mapper = registry().mapper(param.getType(), sourceConf.value(), false)
                    .orElseThrow(() -> new IllegalStateException(String.format("No source mapper found with name '%s' for parameter '%s' in method '%s' in class '%s'.",
                            sourceConf.value(), param.getName(), param.getDeclaringExecutable().getName(), param.getDeclaringExecutable().getDeclaringClass().getSimpleName())));
        } else if ( sourceType.isAssignableFrom(param.getType()) ) {
            return (source) -> source;
        } else {
            mapper = registry().mapper(param.getType(), name, true)
                    .orElse(null);
        }

        if (mapper != null) {
            return mapper;
        }

        throw new IllegalArgumentException(String.format("Cannot find source mapper for parameter '%s' in method '%s' in class '%s'.",
                param.getName(),
                param.getDeclaringExecutable().getName(),
                param.getDeclaringExecutable().getDeclaringClass().getSimpleName()));
    }

    //

    private String invocationErrorMessage(Method method, Object[] arguments) {
        return String.format("Failed to invoke method %s in class %s with arguments of type: %s",
                method.getName() + "(" + Arrays.stream(method.getParameters())
                        .map(p -> p.getType().getSimpleName() + " " + p.getName())
                        .collect(Collectors.joining(", ")) + ")",
                method.getDeclaringClass().getSimpleName(),
                Arrays.stream(arguments)
                        .map(arg -> arg != null ? arg.getClass().getSimpleName() : null)
                        .collect(Collectors.joining(", "))
        );
    }

}
