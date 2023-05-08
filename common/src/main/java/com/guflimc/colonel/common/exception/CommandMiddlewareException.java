package com.guflimc.colonel.common.exception;

import org.jetbrains.annotations.NotNull;

public class CommandMiddlewareException extends RuntimeException {

    private final Runnable handler;

    public CommandMiddlewareException(@NotNull Runnable handler) {
        this.handler = handler;
    }

    public CommandMiddlewareException(String message) {
        this.handler = () -> { throw new RuntimeException(message); };
    }

    public CommandMiddlewareException(String message, Throwable cause) {
        this.handler = () -> { throw new RuntimeException(message, cause); };
    }

    public CommandMiddlewareException(Throwable cause) {
        this.handler = () -> { throw new RuntimeException(cause); };
    }

    public Runnable handler() {
        return handler;
    }
}
