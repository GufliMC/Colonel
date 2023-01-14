package com.guflimc.colonel.common.registry;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SuggestionProviderRegistry<S> {

    public record RegisteredSuggestionProvider<S, T>(@Nullable String name, @NotNull Class<T> type,
                                                     @NotNull SuggestionProvider<S> provider) {}

    private final List<RegisteredSuggestionProvider<S, ?>> providers = new ArrayList<>();

    //

    public <T> void register(@Nullable String name, @NotNull Class<T> type, @NotNull SuggestionProvider<S> provider) {
        providers.add(new RegisteredSuggestionProvider<>(name, type, provider));
    }

    public <T> void register(@NotNull Class<T> type, @NotNull com.mojang.brigadier.suggestion.SuggestionProvider<S> provider) {
        register(null, type, provider);
    }

    //

    public Optional<SuggestionProvider<S>> provider(@Nullable String name, @NotNull Class<?> type) {
        return providers.stream().filter(p -> p.type.equals(type))
                .filter(p -> name == null || name.equals(p.name)) // if name is given, it must match
                .findFirst().map(rsp -> rsp.provider);
    }

}
