package com.guflimc.colonel.common.command.syntax;

import org.jetbrains.annotations.NotNull;

public class CommandParameter<T> {

    private final String name;
    private final CommandParameterType<T> type;

    public CommandParameter(@NotNull String name, @NotNull CommandParameterType<T> type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public CommandParameterType<T> type() {
        return type;
    }

}
