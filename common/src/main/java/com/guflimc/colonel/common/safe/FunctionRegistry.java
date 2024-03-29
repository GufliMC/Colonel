package com.guflimc.colonel.common.safe;

import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionRegistry<S> {

    private final static String DEFAULT = "__DEFAULT__";

    private final Map<FunctionKey, SafeCommandParameterCompleter<S>> completers = new HashMap<>();
    private final Map<FunctionKey, SafeCommandParameterParser<S>> parsers = new HashMap<>();
    private final Map<FunctionKey, SafeCommandSourceMapper<S>> mappers = new HashMap<>();

    //

    private record FunctionKey(@NotNull Class<?> type, @NotNull String name) {

        public FunctionKey(Class<?> type, String name) {
            this.type = wrap(type);
            this.name = name;
        }

    }

    // Completers

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull SafeCommandParameterCompleter<S> completer) {
        remove(completers, type, DEFAULT);
        completers.put(new FunctionKey(type, DEFAULT), completer);
    }

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull Function<String, List<?>> completer) {
        registerParameterCompleter(type, SafeCommandParameterCompleter.withMatchCheck((context, input) -> {
            List<?> result = completer.apply(input);
            return result.stream().map(v -> v instanceof Suggestion s ? s : new Suggestion(v.toString())).toList();
        }));
    }

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull Supplier<List<?>> completer) {
        registerParameterCompleter(type, (context, input) -> {
            List<?> result = completer.get();
            return result.stream().map(v -> v instanceof Suggestion s ? s : new Suggestion(v.toString())).toList();
        });
    }

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull List<?> values) {
        registerParameterCompleter(type, () -> values);
    }

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull String name,
                                           @NotNull SafeCommandParameterCompleter<S> completer) {
        remove(completers, type, name);
        completers.put(new FunctionKey(type, name), completer);
    }

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull String name,
                                           @NotNull Function<String, List<?>> completer) {
        registerParameterCompleter(type, name, SafeCommandParameterCompleter.withMatchCheck((context, input) -> {
            List<?> result = completer.apply(input);
            return result.stream().map(v -> v instanceof Suggestion s ? s : new Suggestion(v.toString())).toList();
        }));
    }

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull String name,
                                           @NotNull Supplier<List<?>> completer) {
        registerParameterCompleter(type, name, (context, input) -> {
            List<?> result = completer.get();
            return result.stream().map(v -> v instanceof Suggestion s ? s : new Suggestion(v.toString())).toList();
        });
    }

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull String name,
                                           @NotNull List<?> values) {
        registerParameterCompleter(type, name, () -> values);
    }

    // Parsers

    public void registerParameterParser(@NotNull Class<?> type,
                                        @NotNull SafeCommandParameterParser<S> parser) {
        remove(parsers, type, DEFAULT);
        parsers.put(new FunctionKey(type, DEFAULT), parser);
    }

    public void registerParameterParser(@NotNull Class<?> type,
                                        @NotNull Function<String, Object> parser) {
        registerParameterParser(type, (context, input) -> parser.apply(input));
    }

    public void registerParameterParser(@NotNull Class<?> type,
                                        @NotNull String name,
                                        @NotNull SafeCommandParameterParser<S> parser) {
        remove(parsers, type, name);
        parsers.put(new FunctionKey(type, name), parser);
    }

    public void registerParameterParser(@NotNull Class<?> type,
                                        @NotNull String name,
                                        @NotNull Function<String, Object> parser) {
        registerParameterParser(type, name, (context, input) -> parser.apply(input));
    }

    // Completers & Parsers

    public void registerParameterType(@NotNull Class<?> type,
                                      @NotNull SafeCommandParameterParser<S> parser,
                                      @NotNull SafeCommandParameterCompleter<S> completer) {
        registerParameterParser(type, parser);
        registerParameterCompleter(type, completer);
    }

    public void registerParameterType(@NotNull Class<?> type,
                                      @NotNull String name,
                                      @NotNull SafeCommandParameterParser<S> parser,
                                      @NotNull SafeCommandParameterCompleter<S> completer) {
        registerParameterParser(type, name, parser);
        registerParameterCompleter(type, name, completer);
    }

    public <T extends SafeCommandParameterParser<S> & SafeCommandParameterCompleter<S>> void registerParameterType(@NotNull Class<?> type,
                                                                                                                   @NotNull T handler) {
        registerParameterType(type, handler, handler);
    }

    public <T extends SafeCommandParameterParser<S> & SafeCommandParameterCompleter<S>> void registerParameterType(@NotNull Class<?> type,
                                                                                                                   @NotNull String name,
                                                                                                                   @NotNull T handler) {
        registerParameterType(type, name, handler, handler);
    }

    // Source mappers

    public void registerSourceMapper(@NotNull Class<?> type,
                                     @NotNull SafeCommandSourceMapper<S> mapper) {
        remove(mappers, type, DEFAULT);
        mappers.put(new FunctionKey(type, DEFAULT), mapper);
    }

    public void registerSourceMapper(@NotNull Class<?> type,
                                     @Nullable String name,
                                     @NotNull SafeCommandSourceMapper<S> mapper) {
        remove(mappers, type, name);
        mappers.put(new FunctionKey(type, name), mapper);
    }

    //

    /**
     * Check for exact type.
     */
    private <T> Optional<T> find(@NotNull Map<FunctionKey, T> map, @NotNull Class<?> type) {
        Class<?> rtype = wrap(type);
        return map.entrySet().stream()
                .filter(e -> rtype.equals(e.getKey().type))
                .min(Comparator.comparingInt(e -> e.getKey().name.equals(DEFAULT) ? 0 : 1))
                .map(Map.Entry::getValue);
    }

    /**
     * Check for exact name and best matching type.
     */
    private <T> Optional<T> find(@NotNull Map<FunctionKey, T> map, @NotNull Class<?> type, @NotNull String name) {
        Class<?> rtype = wrap(type);
        return map.entrySet().stream()
                .filter(e -> Objects.equals(name, e.getKey().name))
                .filter(e -> rtype.isAssignableFrom(e.getKey().type))
                .findFirst()
                .map(Map.Entry::getValue);
    }

    //

    private <T> Optional<T> find(@NotNull Map<FunctionKey, T> map, @NotNull Class<?> type, @NotNull String name, boolean fallback) {
        T result = find(map, type, name).orElse(null);
        if (result != null) {
            return Optional.of(result);
        }
        if (!fallback) {
            return Optional.empty();
        }
        return find(map, type);
    }

    private <T> void remove(@NotNull Map<FunctionKey, T> map, @NotNull Class<?> type, @Nullable String name) {
        Class<?> rtype = wrap(type);
        map.entrySet().stream()
                .filter(e -> Objects.equals(name, e.getKey().name))
                .filter(e -> Objects.equals(e.getKey().type, rtype))
                .findFirst().ifPresent(e -> map.remove(e.getKey()));
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> wrap(Class<T> c) {
        return (Class<T>) MethodType.methodType(c).wrap().returnType();
    }

    // PUBLIC

    public Optional<SafeCommandParameterCompleter<S>> completer(@NotNull Class<?> type, @NotNull String name, boolean fallback) {
        return find(completers, type, name, fallback);
    }

    public Optional<SafeCommandParameterParser<S>> parser(@NotNull Class<?> type, @NotNull String name, boolean fallback) {
        return find(parsers, type, name, fallback);
    }

    public Optional<SafeCommandSourceMapper<S>> mapper(@NotNull Class<?> type, @NotNull String name, boolean fallback) {
        return find(mappers, type, name, fallback);
    }

    public Optional<SafeCommandParameterCompleter<S>> completer(@NotNull Class<?> type) {
        return find(completers, type);
    }

    public Optional<SafeCommandParameterCompleter<S>> completer(@NotNull Class<?> type, @NotNull String name) {
        return find(completers, type, name);
    }

    public Optional<SafeCommandParameterParser<S>> parser(@NotNull Class<?> type) {
        return find(parsers, type);
    }

    public Optional<SafeCommandParameterParser<S>> parser(@NotNull Class<?> type, @NotNull String name) {
        return find(parsers, type, name);
    }

    public Optional<SafeCommandSourceMapper<S>> mapper(@NotNull Class<?> type) {
        return find(mappers, type);
    }

    public Optional<SafeCommandSourceMapper<S>> mapper(@NotNull Class<?> type, @NotNull String name) {
        return find(mappers, type, name);
    }

}
