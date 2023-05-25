package com.guflimc.colonel.common.exception;

public class CommandFailure extends RuntimeException {

    private String command;

    CommandFailure() {
        super();
    }

    CommandFailure(Throwable cause) {
        super(cause);
    }

    public CommandFailure withCommand(String command) {
        this.command = command;
        return this;
    }

    public String command() {
        return command;
    }

}
