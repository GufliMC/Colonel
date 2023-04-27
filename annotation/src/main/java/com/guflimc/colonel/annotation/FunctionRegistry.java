package com.guflimc.colonel.annotation;

import com.guflimc.colonel.common.build.CommandParameterCompleter;
import com.guflimc.colonel.common.build.CommandParameterParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.util.*;

public class FunctionRegistry<S> {

    private final static String DEFAULT = "__DEFAULT__";

    private final Map<FunctionKey, CommandParameterCompleter<S>> completers = new HashMap<>();
    private final Map<FunctionKey, CommandParameterParser<S>> parsers = new HashMap<>();
    private final Map<FunctionKey, CommandSourceMapper<S>> mappers = new HashMap<>();

    //

    private record FunctionKey(@NotNull Class<?> type, @NotNull String name) {

        public FunctionKey(Class<?> type, String name) {
            this.type = wrap(type);
            this.name = name;
        }

    }

    // Completers

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull CommandParameterCompleter<S> completer) {
        remove(mappers, type, DEFAULT);
        completers.put(new FunctionKey(type, DEFAULT), completer);
    }

    public void registerParameterCompleter(@NotNull Class<?> type,
                                           @NotNull String name,
                                           @NotNull CommandParameterCompleter<S> completer) {
        remove(mappers, type, name);
        completers.put(new FunctionKey(type, name), completer);
    }

    // Parsers

    public void registerParameterParser(@NotNull Class<?> type,
                                        @NotNull String name,
                                        @NotNull CommandParameterParser<S> parser) {
        remove(parsers, type, name);
        parsers.put(new FunctionKey(type, name), parser);
    }

    public void registerParameterParser(@NotNull Class<?> type,
                                        @NotNull CommandParameterParser<S> parser) {
        remove(parsers, type, DEFAULT);
        parsers.put(new FunctionKey(type, DEFAULT), parser);
    }

    // Completers & Parsers

    public void registerParameterType(@NotNull Class<?> type,
                                      @NotNull CommandParameterParser<S> parser,
                                      @NotNull CommandParameterCompleter<S> completer) {
        registerParameterParser(type, parser);
        registerParameterCompleter(type, completer);
    }

    public void registerParameterType(@NotNull Class<?> type,
                                      @NotNull String name,
                                      @NotNull CommandParameterParser<S> parser,
                                      @NotNull CommandParameterCompleter<S> completer) {
        registerParameterParser(type, name, parser);
        registerParameterCompleter(type, name, completer);
    }

    public <T extends CommandParameterParser<S> & CommandParameterCompleter<S>> void registerParameterType(@NotNull Class<?> type,
                                                                                                           @NotNull T handler) {
        registerParameterType(type, handler, handler);
    }

    public <T extends CommandParameterParser<S> & CommandParameterCompleter<S>> void registerParameterType(@NotNull Class<?> type,
                                                                                                           @NotNull String name,
                                                                                                           @NotNull T handler) {
        registerParameterType(type, name, handler, handler);
    }

    // Source mappers

    public void registerSourceMapper(@NotNull Class<?> type,
                                     @NotNull CommandSourceMapper<S> mapper) {
        remove(mappers, type, DEFAULT);
        mappers.put(new FunctionKey(type, DEFAULT), mapper);
    }

    public void registerSourceMapper(@NotNull Class<?> type,
                                     @Nullable String name,
                                     @NotNull CommandSourceMapper<S> mapper) {
        remove(mappers, type, name);
        mappers.put(new FunctionKey(type, name), mapper);
    }

    //

    /**
     * Check for best matching type.
     */
    private <T> Optional<T> find(@NotNull Map<FunctionKey, T> map, @NotNull Class<?> type) {
        Class<?> rtype = wrap(type);
        return map.entrySet().stream()
                .filter(e -> e.getKey().type.isAssignableFrom(rtype))
                .min(Comparator.comparingInt(e -> e.getKey().name.equals("DEFAULT") ? 0 : 1))
                .map(Map.Entry::getValue);
    }

    /**
     * Check for exact name and best matching type.
     */
    private <T> Optional<T> find(@NotNull Map<FunctionKey, T> map, @NotNull Class<?> type, @NotNull String name) {
        Class<?> rtype = wrap(type);
        return map.entrySet().stream()
                .filter(e -> Objects.equals(name, e.getKey().name))
                .filter(e -> e.getKey().type.isAssignableFrom(rtype))
                .findFirst()
                .map(Map.Entry::getValue);
    }

    private <T> Optional<T> find(@NotNull Map<FunctionKey, T> map, @NotNull Class<?> type, @NotNull String name, boolean fallback) {
        T result = find(map, type, name).orElse(null);
        if (result != null) {
            return Optional.of(result);
        }
        if ( !fallback ) {
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

    //

    Optional<CommandParameterCompleter<S>> completer(@NotNull Class<?> type, @NotNull String name, boolean fallback) {
        return find(completers, type, name, fallback);
    }

    Optional<CommandParameterParser<S>> parser(@NotNull Class<?> type, @NotNull String name, boolean fallback) {
        return find(parsers, type, name, fallback);
    }

    Optional<CommandSourceMapper<S>> mapper(@NotNull Class<?> type, @NotNull String name, boolean fallback) {
        return find(mappers, type, name, fallback);
    }

}
