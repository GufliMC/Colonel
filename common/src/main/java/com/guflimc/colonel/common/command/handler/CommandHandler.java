package com.guflimc.colonel.common.command.handler;

import com.guflimc.colonel.common.command.CommandContext;

public abstract class CommandHandler {

    private final String[] literals;
    private final CommandParameter<?>[] parameters;

    public CommandHandler(String[] literals, CommandParameter<?>[] parameters) {
        this.literals = literals;
        this.parameters = parameters;
    }

    public String[] literals() {
        return literals;
    }

    public CommandParameter<?>[] parameters() {
        return parameters;
    }

    public abstract void invoke(CommandContext context);

}
