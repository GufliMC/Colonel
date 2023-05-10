package com.guflimc.colonel.common.build.exception;

public class CommandSourceMapException extends CommandHandleException {
    public CommandSourceMapException() {
        super();
    }

    public CommandSourceMapException(String message) {
        super(message);
    }

    public CommandSourceMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandSourceMapException(Throwable cause) {
        super(cause);
    }

    protected CommandSourceMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
