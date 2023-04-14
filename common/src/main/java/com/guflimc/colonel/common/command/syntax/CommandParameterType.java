package com.guflimc.colonel.common.command.syntax;

import org.jetbrains.annotations.NotNull;

public abstract class CommandParameterType<T> implements CommandParameterParser<T>, CommandParameterSuggestionProvider {

    private final Class<T> type;

    protected CommandParameterType(@NotNull Class<T> type) {
        this.type = type;
    }

    public Class<T> type() {
        return type;
    }

}
