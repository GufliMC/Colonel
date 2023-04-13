package com.guflimc.colonel.common.registry;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Registry<U> {

    private record Record<U>(String name, Class<?> type, U data) {}

    private final Set<Record<U>> registry = new HashSet<>();

    public void register(@NotNull String name, @NotNull Class<?> type, @NotNull U data) {
        if ( registry.stream().anyMatch(r -> r.name().equals(name))) {
            throw new IllegalArgumentException(String.format("A record with name '%s' already exists.", name));
        }
        registry.add(new Record<>(name, type, data));
    }

    public <T> void unregister(@NotNull String name) {
        registry.stream()
                .filter(r -> r.name().equals(name))
                .findFirst().ifPresent(registry::remove);
    }

    public Optional<U> find(@NotNull String name) {
        return registry.stream()
                .filter(r -> r.name().equals(name))
                .findFirst()
                .map(Record::data);
    }

    public Optional<U> find(@NotNull Class<?> type) {
        return registry.stream()
                .filter(r -> r.type().equals(type))
                .findFirst()
                .map(Record::data);
    }

    public Optional<U> find(@NotNull String name, @NotNull Class<?> type) {
        return registry.stream()
                .filter(r -> r.name().equals(name))
                .filter(r -> r.type().equals(type))
                .findFirst()
                .map(Record::data);
    }
}
