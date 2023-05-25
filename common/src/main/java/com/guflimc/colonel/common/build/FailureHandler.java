package com.guflimc.colonel.common.build;

import org.jetbrains.annotations.NotNull;

public class FailureHandler extends RuntimeException {

    private final Runnable handler;

    private FailureHandler(Runnable handler) {
        this.handler = handler;
    }

    public Runnable handler() {
        return handler;
    }

    //

    public static FailureHandler of(@NotNull Runnable handler) {
        return new FailureHandler(handler);
    }

}
