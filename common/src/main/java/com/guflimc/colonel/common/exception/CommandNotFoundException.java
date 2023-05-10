package com.guflimc.colonel.common.exception;

public class CommandNotFoundException extends CommandDispatchException {

    public CommandNotFoundException() {
        super();
    }

    public CommandNotFoundException(String message) {
        super(message);
    }

    public CommandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandNotFoundException(Throwable cause) {
        super(cause);
    }

    protected CommandNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
