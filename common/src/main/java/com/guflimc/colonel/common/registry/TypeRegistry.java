package com.guflimc.colonel.common.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.util.*;

public class TypeRegistry<U> {

    private record Record<U>(@Nullable String name, @NotNull Class<?> type, @NotNull U data) {}

    private final Set<Record<U>> registry = new LinkedHashSet<>();

    public void register(@NotNull Class<?> type, @NotNull String name, @NotNull U data) {
        if ( registry.stream().anyMatch(r -> r.type().equals(type) && name.equals(r.name()))) {
            throw new IllegalArgumentException(String.format("A record with name '%s' for type %s already exists.", name, type.getSimpleName()));
        }
        Class<?> rtype = type.isPrimitive() ? wrap(type) : type;
        registry.add(new Record<>(name, rtype, data));
    }

    public void register(@NotNull Class<?> type, @NotNull U data) {
        if ( registry.stream().anyMatch(r -> r.type().equals(type) && r.name() == null)) {
            throw new IllegalArgumentException(String.format("A record with no name and type %s already exists.", type.getSimpleName()));
        }
        Class<?> rtype = type.isPrimitive() ? wrap(type) : type;
        registry.add(new Record<>(null, rtype, data));
    }

    public <T> void unregister(@NotNull Class<?> type, @NotNull String name) {
        registry.stream()
                .filter(r -> name.equals(r.name()))
                .filter(r -> r.type().equals(type))
                .findFirst().ifPresent(registry::remove);
    }

    public <T> void unregister(@NotNull Class<?> type) {
        registry.stream()
                .filter(r -> r.type().equals(type))
                .min(Comparator.comparing(r -> r.name() == null ? "" : r.name())) // type with no name first
                .ifPresent(registry::remove);
    }

    public Optional<U> find(@NotNull String name) {
        return registry.stream()
                .filter(r -> name.equals(r.name()))
                .findFirst()
                .map(Record::data);
    }

    public Optional<U> find(@NotNull Class<?> type) {
        Class<?> rtype = type.isPrimitive() ? wrap(type) : type;
        return registry.stream()
                .filter(r -> r.type().equals(rtype))
                .min(Comparator.comparing(r -> r.name() == null ? "" : r.name()))
                .map(Record::data);
    }

    public Optional<U> find(@NotNull Class<?> type, @NotNull String name) {
        Class<?> rtype = type.isPrimitive() ? wrap(type) : type;
        U data = registry.stream()
                .filter(r -> name.equals(r.name()))
                .filter(r -> r.type().equals(rtype))
                .findFirst()
                .map(Record::data).orElse(null);
        if (data == null) {
            return find(type); // fallback to just type
        }
        return Optional.of(data);
    }

    //

    @SuppressWarnings("unchecked")
    public static <T> Class<T> wrap(Class<T> c) {
        return (Class<T>) MethodType.methodType(c).wrap().returnType();
    }

}
