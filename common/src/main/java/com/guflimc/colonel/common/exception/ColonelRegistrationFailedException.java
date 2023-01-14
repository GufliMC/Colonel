package com.guflimc.colonel.common.exception;

public class ColonelRegistrationFailedException extends ColonelCommandExeception {

    public ColonelRegistrationFailedException() {
        super();
    }

    public ColonelRegistrationFailedException(String message) {
        super(message);
    }

    public ColonelRegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ColonelRegistrationFailedException(Throwable cause) {
        super(cause);
    }
}
