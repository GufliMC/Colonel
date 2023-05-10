package com.guflimc.colonel.common.build.exception;

public class CommandHandleException extends RuntimeException {

    public CommandHandleException() {
        super();
    }

    public CommandHandleException(String message) {
        super(message);
    }

    public CommandHandleException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandHandleException(Throwable cause) {
        super(cause);
    }

    protected CommandHandleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
