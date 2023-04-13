package com.guflimc.colonel.common.command.syntax;

import org.jetbrains.annotations.NotNull;

public class CommandSyntax {

    private final String[] literals;
    private final CommandParameter<?>[] parameters;

    public CommandSyntax(@NotNull String[] literals, @NotNull CommandParameter<?>[] parameters) {
        this.literals = literals;
        this.parameters = parameters;
    }

    public String[] literals() {
        return literals;
    }

    public CommandParameter<?>[] parameters() {
        return parameters;
    }

}
