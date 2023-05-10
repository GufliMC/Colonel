package com.guflimc.colonel.common.build.exception;

public class CommandParameterParseException extends CommandHandleException {
    public CommandParameterParseException() {
        super();
    }

    public CommandParameterParseException(String message) {
        super(message);
    }

    public CommandParameterParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandParameterParseException(Throwable cause) {
        super(cause);
    }

    protected CommandParameterParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
