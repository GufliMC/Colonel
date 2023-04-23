package com.guflimc.colonel.common.build;

public sealed class Argument permits Argument.ArgumentSuccess, Argument.ArgumentFailure {

    public static Argument success(Object value) {
        return new ArgumentSuccess(value);
    }

    public static Argument fail(Runnable runnable) {
        return new ArgumentFailure(runnable);
    }

    //

    static final class ArgumentSuccess extends Argument {

        final Object value;

        ArgumentSuccess(Object value) {
            this.value = value;
        }

    }

    static final class ArgumentFailure extends Argument {

        final Runnable runnable;

        ArgumentFailure(Runnable runnable) {
            this.runnable = runnable;
        }
    }

}
