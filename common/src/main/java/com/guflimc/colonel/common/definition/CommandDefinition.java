package com.guflimc.colonel.common.definition;

public class CommandDefinition {

    private final CommandParameter[] parameters;

    public CommandDefinition(CommandParameter[] parameters) {
        // TODO check for duplicate names
        this.parameters = parameters;
    }

    public CommandParameter[] parameters() {
        return parameters;
    }

}
