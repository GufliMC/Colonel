package com.gufli.colonel.common.exception;

import com.gufli.colonel.common.dispatch.definition.CommandDefinition;

public class CommandHandleFailure extends CommandDispatchFailure {

    private CommandDefinition definition;

    CommandHandleFailure(Throwable cause) {
        super(cause);
    }

    public CommandHandleFailure withDefinition(CommandDefinition definition) {
        this.definition = definition;
        return this;
    }

    public CommandDefinition definition() {
        return definition;
    }

}
