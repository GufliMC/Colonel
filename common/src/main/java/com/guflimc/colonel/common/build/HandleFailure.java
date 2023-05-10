package com.guflimc.colonel.common.build;

public class HandleFailure extends RuntimeException {

    private final Runnable handler;

    private HandleFailure(Runnable handler) {
        this.handler = handler;
    }

    public Runnable handler() {
        return handler;
    }

    //

    public static HandleFailure of(Runnable handler) {
        return new HandleFailure(handler);
    }

    public static HandleFailure of(RuntimeException ex) {
        return new HandleFailure(() -> { throw ex; });
    }

    public static HandleFailure of(Exception ex) {
        return of(new RuntimeException(ex));
    }

    public static HandleFailure of(String message) {
        return of(new RuntimeException(message));
    }
}
