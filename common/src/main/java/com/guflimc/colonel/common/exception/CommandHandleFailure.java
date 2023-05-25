package com.guflimc.colonel.common.exception;

import com.guflimc.colonel.common.dispatch.definition.CommandDefinition;

public class CommandHandleFailure extends CommandDispatchFailure {

    private CommandDefinition definition;

    CommandHandleFailure(Throwable cause) {
        super(cause);
    }

    CommandHandleFailure withDefinition(CommandDefinition definition) {
        this.definition = definition;
        return this;
    }

    public CommandDefinition definition() {
        return definition;
    }

}
