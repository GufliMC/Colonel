package com.guflimc.colonel.common.build.exception;

public class CommandParameterCompleteException extends CommandHandleException {
    public CommandParameterCompleteException() {
        super();
    }

    public CommandParameterCompleteException(String message) {
        super(message);
    }

    public CommandParameterCompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandParameterCompleteException(Throwable cause) {
        super(cause);
    }

    protected CommandParameterCompleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
