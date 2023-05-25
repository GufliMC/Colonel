package com.guflimc.colonel.common.exception;

import com.guflimc.colonel.common.dispatch.definition.CommandParameter;

public class CommandParameterFailure extends CommandHandleFailure {

    private CommandParameter parameter;
    private String input;

    CommandParameterFailure(Throwable cause) {
        super(cause);
    }

    public CommandParameterFailure withParameter(CommandParameter parameter) {
        this.parameter = parameter;
        return this;
    }

    public CommandParameterFailure withInput(String input) {
        this.input = input;
        return this;
    }

    public CommandParameter parameter() {
        return parameter;
    }

    public String input() {
        return input;
    }

}
