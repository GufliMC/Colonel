package com.guflimc.colonel.common.exception;

public class ColonelCommandExeception extends RuntimeException {

    public ColonelCommandExeception() {
        super();
    }

    public ColonelCommandExeception(String message) {
        super(message);
    }

    public ColonelCommandExeception(String message, Throwable cause) {
        super(message, cause);
    }

    public ColonelCommandExeception(Throwable cause) {
        super(cause);
    }
}
