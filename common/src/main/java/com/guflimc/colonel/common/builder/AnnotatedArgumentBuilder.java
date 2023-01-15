package com.guflimc.colonel.common.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;

public class AnnotatedArgumentBuilder<S, T> {

    private final Parameter parameter;

    private SuggestionProvider<S> suggestions = null;
    private ArgumentType<T> argumentType = null;

    public AnnotatedArgumentBuilder(@NotNull Parameter parameter) {
        this.parameter = parameter;
    }

    public AnnotatedArgumentBuilder<S, T> withSuggestions(SuggestionProvider<S> suggestions) {
        this.suggestions = suggestions;
        return this;
    }

    public AnnotatedArgumentBuilder<S, T> withArgumentType(@NotNull ArgumentType<T> argumentType) {
        this.argumentType = argumentType;
        return this;
    }

    RequiredArgumentBuilder<S, T> build() {
        if ( argumentType == null ) {
            throw new IllegalArgumentException("Argument type must not be null.");
        }

        RequiredArgumentBuilder<S, T> builder = RequiredArgumentBuilder.argument(parameter.getName(), argumentType);
        if ( suggestions != null ) {
            builder.suggests(suggestions);
        }
        return builder;
    }



}
