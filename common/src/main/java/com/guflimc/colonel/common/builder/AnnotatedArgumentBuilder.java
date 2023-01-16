package com.guflimc.colonel.common.builder;

import com.guflimc.colonel.common.ColonelConfig;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

public class AnnotatedArgumentBuilder<S, T> {

    private final ColonelConfig<S> config;
    private final String name;

    private SuggestionProvider<S> suggestions = null;
    private ArgumentType<T> argumentType = null;

    AnnotatedArgumentBuilder(@NotNull ColonelConfig<S> config, @NotNull String name) {
        this.config = config;
        this.name = name;
    }

    //

    public AnnotatedArgumentBuilder<S, T> withSuggestions(@NotNull SuggestionProvider<S> suggestions) {
        this.suggestions = suggestions;
        return this;
    }

    public AnnotatedArgumentBuilder<S, T> withArgumentType(@NotNull ArgumentType<T> argumentType) {
        this.argumentType = argumentType;
        return this;
    }

    //

    RequiredArgumentBuilder<S, T> build() {
        if (argumentType == null) {
            throw new IllegalArgumentException("Argument type must not be null.");
        }

        RequiredArgumentBuilder<S, T> builder = RequiredArgumentBuilder.argument(name, argumentType);
        if (suggestions != null) {
            builder.suggests(suggestions);
        }

        return builder;
    }


}
