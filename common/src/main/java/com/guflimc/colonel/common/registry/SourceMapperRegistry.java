package com.guflimc.colonel.common.registry;

import com.mojang.brigadier.context.CommandContext;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SourceMapperRegistry<S> {

    private final Map<Class<?>, Mapper<S, ?>> mappers = new ConcurrentHashMap<>();

    @FunctionalInterface
    public interface Mapper<S, T> {
        T map(CommandContext<S> context);
    }

    //

    public <T> void register(Class<T> type, Mapper<S, T> mapper) {
        mappers.put(type, mapper);
    }

    public Optional<Object> map(CommandContext<S> context, Class<?> type) {
        return find(type).map(mapper -> mapper.map(context));
    }

    //

    private Optional<Mapper<S, ?>> find(Class<?> type) {
        for (Class<?> cls : mappers.keySet()) {
            if (cls.equals(type)) {
                return Optional.ofNullable(mappers.get(type));
            }
        }

        for (Class<?> cls : mappers.keySet()) {
            if (cls.isAssignableFrom(type)) {
                return Optional.ofNullable(mappers.get(type));
            }
        }

        return Optional.empty();
    }

    //

    public SourceMapperRegistry() {

    }

    //


}
