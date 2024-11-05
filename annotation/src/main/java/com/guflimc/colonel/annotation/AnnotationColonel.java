package com.guflimc.colonel.annotation;

import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Input;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.dispatch.definition.ReadMode;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.safe.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        Map<Parameter, Function<SafeCommandContext<S>, Object>> suppliers = new LinkedHashMap<>();

        SafeCommandHandlerBuilder<S> builder = builder();
        build(method, suppliers, builder);

        // set executor
        builder.executor(ctx -> {
            Object[] arguments = suppliers.values().stream()
                    .map(f -> f.apply(ctx))
                    .toArray();

            try {
                method.invoke(container, arguments);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(invocationErrorMessage(method, arguments), e);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });

        // register
        builder.register();
    }

    protected void build(@NotNull Method method, @NotNull Map<Parameter, Function<SafeCommandContext<S>, Object>> suppliers, @NotNull SafeCommandHandlerBuilder<S> builder) {
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

        int mi = 0;
        for (Parameter param : method.getParameters()) {
            // SOURCE
            if (param.isAnnotationPresent(Source.class)) {
                SafeCommandSourceMapper<S> mapper = sourceMapper(param);
                builder.source(mapper);
                build(param, suppliers, mi++);
                continue;
            }

            // PARAMETER
            try {
                SafeCommandParameterBuilder<S> pb = builder.parameter();
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

    protected void build(@NotNull Parameter parameter, @NotNull Map<Parameter, Function<SafeCommandContext<S>, Object>> suppliers, int index) {
        suppliers.put(parameter, ctx -> ctx.source(index));
    }

    protected void build(@NotNull Parameter parameter, @NotNull Map<Parameter, Function<SafeCommandContext<S>, Object>> suppliers, @NotNull SafeCommandParameterBuilder<S> builder) {
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
        if (paramConf != null && !paramConf.parser().isEmpty()) {
            builder.parser(parameter.getType(), paramConf.parser());
        } else {
            builder.parser(parameter.getType());
        }

        // completer
        if (paramConf != null && !paramConf.completer().isEmpty()) {
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

        if (!List.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalArgumentException(String.format("Completer method '%s' in class '%s' must return a List.",
                    method.getName(), container.getClass().getSimpleName()));
        }

        Map<Parameter, BiFunction<SafeCommandContext<S>, String, Object>> suppliers = suppliers(method);

        Completer completerConf = method.getAnnotation(Completer.class);
        String name = method.getName();
        if (!completerConf.value().isEmpty()) {
            name = completerConf.value();
        }

        SafeCommandParameterCompleter<S> completer = (context, input) -> {
            Object[] arguments = suppliers.values().stream()
                    .map(f -> f.apply(context, input))
                    .toArray();

            List<?> result;
            try {
                result = (List<?>) method.invoke(container, arguments);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(invocationErrorMessage(method, arguments), e);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }

            return result.stream().map(v -> v instanceof Suggestion s ? s : new Suggestion(v.toString())).toList();
        };
        completer = SafeCommandParameterCompleter.withMatchCheck(completer);

        Class<?> type = completerConf.type() != Void.class ? completerConf.type() : Object.class;
        registry().registerParameterCompleter(type, name, completer);
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

        Map<Parameter, BiFunction<SafeCommandContext<S>, String, Object>> suppliers = suppliers(method);

        Parser parserConf = method.getAnnotation(Parser.class);
        String name = method.getName();
        if (!parserConf.value().isEmpty()) {
            name = parserConf.value();
        }

        SafeCommandParameterParser<S> parser = (context, input) -> {
            Object[] arguments = suppliers.values().stream()
                    .map(f -> f.apply(context, input))
                    .toArray();

            try {
                return method.invoke(container, arguments);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(invocationErrorMessage(method, arguments), e);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        };

        Class<?> type = parserConf.type() != Void.class ? parserConf.type() : method.getReturnType();
        registry().registerParameterParser(type, name, parser);
    }

    /**
     * Creates a map which defines for each parameter, how to retrieve the correct value. This only works for utility
     * methods like parsers and completers.
     */
    private Map<Parameter, BiFunction<SafeCommandContext<S>, String, Object>> suppliers(@NotNull Method method) {
        Map<Parameter, BiFunction<SafeCommandContext<S>, String, Object>> suppliers = new LinkedHashMap<>();

        for (Parameter param : method.getParameters()) {
            // source
            if (param.isAnnotationPresent(Source.class)) {
                SafeCommandSourceMapper<S> mapper = sourceMapper(param);
                suppliers.put(param, (ctx, input) -> mapper.map(ctx.source()));
                continue;
            }

            // input
            if (param.isAnnotationPresent(Input.class)
                    || param.getName().equals("input") // TODO backwards compatibility, @Input should be used instead
            ) {
                suppliers.put(param, (ctx, input) -> input);
                continue;
            }

            // parameter
            com.guflimc.colonel.annotation.annotations.parameter.Parameter paramConf = param
                    .getAnnotation(com.guflimc.colonel.annotation.annotations.parameter.Parameter.class);
            String name;
            if (paramConf != null && !paramConf.value().isEmpty()) {
                name = paramConf.value();
            } else {
                name = param.getName();
            }
            suppliers.put(param, (ctx, input) -> ctx.argument(name));
        }

        return suppliers;
    }

    /**
     * Returns a mapper for the given parameter based on it's {@link Source} annotation.
     */
    private SafeCommandSourceMapper<S> sourceMapper(@NotNull Parameter param) {
        com.guflimc.colonel.annotation.annotations.parameter.Parameter paramConf = param
                .getAnnotation(com.guflimc.colonel.annotation.annotations.parameter.Parameter.class);

        // name
        String name = param.getName();
        if (paramConf != null && !paramConf.value().isEmpty()) {
            name = paramConf.value();
        }

        Source sourceConf = param.getAnnotation(Source.class);
        SafeCommandSourceMapper<S> mapper;
        if (!sourceConf.value().isEmpty()) {
            mapper = registry().mapper(param.getType(), sourceConf.value(), false)
                    .orElseThrow(() -> new IllegalStateException(String.format("No source mapper found with name '%s' for parameter '%s' of type %s in method '%s' in class '%s'.",
                            sourceConf.value(), param.getName(), param.getType().getSimpleName(), param.getDeclaringExecutable().getName(),
                            param.getDeclaringExecutable().getDeclaringClass().getSimpleName())));
        } else if (sourceType.isAssignableFrom(param.getType())) {
            return (source) -> source;
        } else {
            mapper = registry().mapper(param.getType(), name, true)
                    .orElse(null);
        }

        if (mapper != null) {
            return mapper;
        }

        throw new IllegalArgumentException(String.format("Cannot find source mapper for parameter '%s' of type %s in method '%s' in class '%s'.",
                param.getName(),
                param.getType().getSimpleName(),
                param.getDeclaringExecutable().getName(),
                param.getDeclaringExecutable().getDeclaringClass().getSimpleName()));
    }

    //

    private String invocationErrorMessage(Method method, Object[] arguments) {
        return String.format("Failed to invoke method %s in class %s with arguments: %s",
                method.getName() + "(" + Arrays.stream(method.getParameters())
                        .map(p -> p.getType().getSimpleName() + " " + p.getName())
                        .collect(Collectors.joining(", ")) + ")",
                method.getDeclaringClass().getSimpleName(),
                Arrays.stream(arguments)
                        .map(arg -> arg != null ? arg + " (" + arg.getClass().getSimpleName() + ")" : null)
                        .collect(Collectors.joining(", "))
        );
    }

}
