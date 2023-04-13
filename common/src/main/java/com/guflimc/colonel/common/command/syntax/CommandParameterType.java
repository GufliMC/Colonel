package com.guflimc.colonel.common.command.syntax;

import org.jetbrains.annotations.NotNull;

public abstract class CommandParameterType<T> implements CommandParameterParser<T>, CommandParameterSuggestionProvider {

    private final String name;
    private final Class<T> type;

    protected CommandParameterType(@NotNull String name, @NotNull Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public Class<T> type() {
        return type;
    }

}
