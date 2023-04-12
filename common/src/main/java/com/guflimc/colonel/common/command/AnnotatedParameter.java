package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.ColonelConfig;
import com.mojang.brigadier.context.CommandContext;

public abstract class AnnotatedParameter<S> {

    private final String name;
    private final Class<?> type;

    protected AnnotatedParameter(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public Class<?> type() {
        return type;
    }

    public abstract <R> R parse(ColonelConfig<S> config, CommandContext<S> context);

}