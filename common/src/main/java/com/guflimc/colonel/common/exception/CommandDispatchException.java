package com.guflimc.colonel.common.exception;

public class CommandDispatchException extends RuntimeException {

    public CommandDispatchException() {
        super();
    }

    public CommandDispatchException(String message) {
        super(message);
    }

    public CommandDispatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandDispatchException(Throwable cause) {
        super(cause);
    }

    protected CommandDispatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
