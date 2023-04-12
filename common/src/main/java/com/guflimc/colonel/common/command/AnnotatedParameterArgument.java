package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.ColonelConfig;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;

import java.lang.reflect.Parameter;

public class AnnotatedParameterArgument<S, T> extends AnnotatedParameter<S> {

    // mappings to brigadier
    private final ArgumentType<T> argumentType;
    private final SuggestionProvider<S> suggestionProvider;

    public AnnotatedParameterArgument(Parameter parameter) {
        super(parameter);

    }

    public CommandNode<S> node() {
        RequiredArgumentBuilder<S, T> b = RequiredArgumentBuilder.argument(parameter().getName(), argumentType);
        if ( suggestionProvider != null ) b.suggests(suggestionProvider);
        return b.build();
    }

    public <R> R parse(ColonelConfig<S> config, CommandContext<S> context) {
        // TODO
        return null;
    }

    //

    public ArgumentType<Integer> integer() {
        return ArgumentType.integer();
    }

}
