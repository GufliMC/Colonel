package com.guflimc.colonel.annotation;

import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.build.*;
import com.guflimc.colonel.common.definition.CommandParameter;
import com.guflimc.colonel.common.suggestion.Suggestion;
import com.guflimc.colonel.common.tree.CommandHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AnnotationColonel<S> extends Colonel<S> {

    private record FunctionKey(Class<?> type, String name) {
    }

    private final Map<FunctionKey, CommandParameterCompleter<S>> completers = new HashMap<>();
    private final Map<FunctionKey, CommandParameterParser<S>> parsers = new HashMap<>();
    private final Map<FunctionKey, CommandSourceMapper<S>> mappers = new HashMap<>();

    public AnnotationColonel() {

        // REGISTER DEFAULT JAVA TYPES
        Function<BiFunction<CommandContext<S>, String, Object>, CommandParameterParser<S>> wrap = (func) -> (ctx, value) -> {
            try {
                return Argument.success(func.apply(ctx, value));
            } catch (Throwable e) {
                return Argument.fail(() -> {
                    throw e;
                });
            }
        };

        registerParameterParser(String.class, null, (ctx, value) -> Argument.success(value));
        registerParameterParser(Integer.class, null, wrap.apply((ctx, value) -> Integer.parseInt(value)));
        registerParameterParser(Long.class, null, wrap.apply((ctx, value) -> Long.parseLong(value)));
        registerParameterParser(Float.class, null, wrap.apply((ctx, value) -> Float.parseFloat(value)));
        registerParameterParser(Double.class, null, wrap.apply((ctx, value) -> Double.parseDouble(value)));
        registerParameterParser(Byte.class, null, wrap.apply((ctx, value) -> Byte.parseByte(value)));
        registerParameterParser(Short.class, null, wrap.apply((ctx, value) -> Short.parseShort(value)));
        registerParameterParser(Instant.class, null, wrap.apply((ctx, value) -> Instant.parse(value)));
        registerParameterParser(LocalTime.class, null, wrap.apply((ctx, value) -> LocalTime.parse(value)));
        registerParameterParser(LocalDate.class, null, wrap.apply((ctx, value) -> LocalDate.parse(value)));
        registerParameterParser(LocalDateTime.class, null, wrap.apply((ctx, value) -> LocalDateTime.parse(value)));
        registerParameterParser(UUID.class, null, wrap.apply((ctx, value) -> UUID.fromString(value)));
        registerParameterParser(Boolean.class, null, (ctx, value) -> {
            if (value.equalsIgnoreCase("true") || value.equals("1")
                    || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {
                return Argument.success(true);
            }
            if (value.equalsIgnoreCase("false") || value.equals("0")
                    || value.equalsIgnoreCase("n") || value.equalsIgnoreCase("no")) {
                return Argument.success(false);
            }
            return Argument.fail(() -> {
                throw new IllegalArgumentException("Invalid boolean value: " + value);
            });
        });
    }

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
        method.setAccessible(true);

        Command[] commands = method.getAnnotationsByType(Command.class);
        for (Command cmd : commands) {
            if (cmd == null || cmd.value().trim().length() == 0) {
                throw new IllegalArgumentException(String.format("Command annotation is empty for method '%s' in class '%s'.",
                        method.getName(), container.getClass().getSimpleName()));
            }
        }

        CommandHandlerBuilder<S> builder = new CommandHandlerBuilder<>();

        Map<Parameter, Function<CommandContext<S>, Object>> suppliers = new LinkedHashMap<>();
        for (Parameter param : method.getParameters()) {
            if ( param.isAnnotationPresent(Source.class) ) {
                CommandSourceMapper<S> mapper = sourceMapper(param);
                suppliers.put(param, ctx -> mapper.map(ctx.source()));
                continue;
            }

            com.guflimc.colonel.annotation.annotations.parameter.Parameter paramConf = param.getAnnotation(com.guflimc.colonel.annotation.annotations.parameter.Parameter.class);

            // name
            String name = param.getName();
            if (paramConf != null && !paramConf.value().isEmpty()) {
                name = paramConf.value();
            }

            // read mode
            CommandParameter.ReadMode mode = CommandParameter.ReadMode.STRING;
            if (paramConf != null) {
                mode = paramConf.read();
            }

            // parser
            CommandParameterParser<S> parser;
            if (paramConf != null && !paramConf.parser().isEmpty()) {
                parser = find(parsers, param.getType(), paramConf.parser())
                        .orElseThrow(() -> new IllegalArgumentException(String.format("No parser with name '%s' found for parameter '%s' in method '%s' in class '%s'.",
                                paramConf.parser(), param.getName(), method.getName(), container.getClass().getSimpleName())));
            } else {
                // find by name of parameter
                parser = find(parsers, param.getType(), name).orElse(null);

                // fallback to only type
                if (parser == null) {
                    parser = find(parsers, param.getType(), null)
                            .orElseThrow(() -> new IllegalArgumentException(String.format("No parser found for type of parameter '%s' in method '%s' in class '%s'.",
                                    param.getName(), method.getName(), container.getClass().getSimpleName())));
                }
            }

            // completer
            CommandParameterCompleter<S> completer;
            if (paramConf != null && !paramConf.completer().isEmpty()) {
                completer = find(completers, param.getType(), paramConf.completer())
                        .orElseThrow(() -> new IllegalArgumentException(String.format("No completer with name '%s' found for parameter '%s' in method '%s' in class '%s'.",
                                paramConf.completer(), param.getName(), method.getName(), container.getClass().getSimpleName())));
            } else {
                // find by name of parameter
                completer = find(completers, param.getType(), name).orElse(null);

                // fallback to only type
                if ( completer == null ) {
                    completer = find(completers, param.getType(), null)
                            .orElseGet(() -> (context, input) -> List.of());
                }
            }
            completer = CommandParameterCompleter.startsWith(completer);

            // register
            CommandParameter cp = new CommandParameter(name, mode);
            builder.parameter(cp, parser, completer);
            suppliers.put(param, ctx -> ctx.argument(cp));
        }

        build(method, builder);

        builder.executor(ctx -> {
            try {
                method.invoke(container, suppliers.values().stream().map(f -> f.apply(ctx)).toArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        CommandHandler handler = builder.build();
        for (Command cmd : commands) {
            register(cmd.value(), handler);
        }
    }

    protected void build(@NotNull Method method, @NotNull CommandHandlerBuilder<S> builder) {}

    //

    private void registerCompleter(@NotNull Method method, @NotNull Object container) {
        method.setAccessible(true);

        if (!method.getReturnType().equals(List.class)) {
            throw new IllegalArgumentException(String.format("Completer method '%s' in class '%s' must return a List.",
                    method.getName(), container.getClass().getSimpleName()));
        }

        Map<Parameter, BiFunction<CommandContext<S>, String, Object>> suppliers = suppliers(method);

        Completer completerConf = method.getAnnotation(Completer.class);
        String name = method.getName();
        if (completerConf != null && !completerConf.value().isEmpty()) {
            name = completerConf.value();
        }

        CommandParameterCompleter<S> completer = (context, input) -> {
            List<?> result;
            try {
                result = (List<?>) method.invoke(container, suppliers.values().stream().map(f -> f.apply(context, input)).toArray());
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

        registerParameterCompleter(name, completer);
    }

    private void registerParser(@NotNull Method method, @NotNull Object container) {
        method.setAccessible(true);

        if (method.getReturnType().equals(Void.TYPE)) {
            throw new IllegalArgumentException(String.format("Parser method '%s' in class '%s' must return something.",
                    method.getName(), container.getClass().getSimpleName()));
        }

        Map<Parameter, BiFunction<CommandContext<S>, String, Object>> suppliers = suppliers(method);

        Parser parserConf = method.getAnnotation(Parser.class);
        String name = method.getName();
        if (parserConf != null && !parserConf.value().isEmpty()) {
            name = parserConf.value();
        }

        CommandParameterParser<S> parser = (context, input) -> {
            Object value;
            try {
                value = method.invoke(container, suppliers.values().stream().map(f -> f.apply(context, input)).toArray());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (value instanceof Argument arg) {
                return arg;
            }
            return Argument.success(value);
        };

        registerParameterParser(method.getReturnType(), name, parser);
    }

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
            // find for given name
            mapper = find(mappers, param.getType(), sourceConf.value()).orElse(null);
        } else {
            // find for parameter name
            mapper = find(mappers, param.getType(), name).orElse(null);

            // fallback to type only
            if ( mapper == null ) {
                mapper = find(mappers, param.getType(), null).orElse(null);
            }
        }

        if ( mapper == null ) {
            mapper = s -> {
                if (param.getType().isInstance(s)) {
                    return s;
                }
                throw new IllegalStateException(String.format("No source mapper found for parameter '%s' in method '%s' in class '%s'.",
                        param.getName(), param.getDeclaringExecutable().getName(), param.getDeclaringExecutable().getDeclaringClass().getSimpleName()));
            };
        }
        return mapper;
    }

    private Map<Parameter, BiFunction<CommandContext<S>, String, Object>> suppliers(@NotNull Method method) {
        Map<Parameter, BiFunction<CommandContext<S>, String, Object>> suppliers = new LinkedHashMap<>();
        for (Parameter param : method.getParameters()) {

            // parameter is annotated
            if ( param.isAnnotationPresent(Source.class) ) {
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
            throw new IllegalArgumentException(String.format("Utility method '%s' in class '%s' may only have a context and input parameter.",
                    method.getName(), method.getDeclaringClass().getSimpleName()));
        }

        return suppliers;
    }

    //

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull String name,
                                           @NotNull CommandParameterCompleter<S> completer) {
        type = wrap(type);
        completers.put(new FunctionKey(type, name), completer);
    }

    public void registerParameterCompleter(@NotNull String name,
                                           @NotNull CommandParameterCompleter<S> completer) {
        completers.put(new FunctionKey(null, name), completer);
    }

    public void registerParameterParser(@NotNull Class<?> type,
                                        @NotNull CommandParameterParser<S> parser) {
        registerParameterParser(type, null, parser);
    }

    public void registerParameterParser(@NotNull Class<?> type,
                                        @Nullable String name,
                                        @NotNull CommandParameterParser<S> parser) {
        type = wrap(type);
        parsers.put(new FunctionKey(type, name), parser);
    }

    public void registerParameterType(@NotNull Class<?> type,
                                      @NotNull CommandParameterCompleter<S> completer,
                                      @NotNull CommandParameterParser<S> parser) {
        registerParameterType(type, null, completer, parser);
    }

    public void registerParameterType(@NotNull Class<?> type,
                                      @Nullable String name,
                                      @NotNull CommandParameterCompleter<S> completer,
                                      @NotNull CommandParameterParser<S> parser) {
        registerParameterParser(type, name, parser);
        if (name != null) {
            registerParameterCompleter(type, name, completer);
        }
    }

    public void registerSourceMapper(@NotNull Class<?> type,
                                     @NotNull CommandSourceMapper<S> mapper) {
        registerSourceMapper(type, null, mapper);
    }

    public void registerSourceMapper(@NotNull Class<?> type,
                                     @Nullable String name,
                                     @NotNull CommandSourceMapper<S> mapper) {
        type = wrap(type);
        mappers.put(new FunctionKey(type, name), mapper);
    }

    //

    public <T> Optional<T> find(@NotNull Map<FunctionKey, T> map, @NotNull Class<?> type, @Nullable String name) {
        Class<?> rtype = wrap(type);
        return map.entrySet().stream()
                .filter(e -> e.getKey().type == null || e.getKey().type.isAssignableFrom(rtype))
                .filter(e -> (name == null && e.getKey().name == null) || Objects.equals(name, e.getKey().name))
                .findFirst()
                .map(Map.Entry::getValue);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrap(Class<T> c) {
        return (Class<T>) MethodType.methodType(c).wrap().returnType();
    }
}
