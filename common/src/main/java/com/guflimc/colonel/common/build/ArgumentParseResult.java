package com.guflimc.colonel.common.build;

public sealed class ArgumentParseResult permits ArgumentParseResult.ArgumentParseSuccess, ArgumentParseResult.ArgumentParseFail {

    public static ArgumentParseResult success(Object value) {
        return new ArgumentParseSuccess(value);
    }

    public static ArgumentParseResult fail(Runnable runnable) {
        return new ArgumentParseFail(runnable);
    }

    //

    static final class ArgumentParseSuccess extends ArgumentParseResult {

        final Object value;

        ArgumentParseSuccess(Object value) {
            this.value = value;
        }

    }

    static final class ArgumentParseFail extends ArgumentParseResult {

        final Runnable runnable;

        ArgumentParseFail(Runnable runnable) {
            this.runnable = runnable;
        }
    }

}
