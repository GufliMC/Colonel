package com.gufli.colonel.common.dispatch.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed class CommandInputArgument permits CommandInputArgument.ArgumentSuccess, CommandInputArgument.ArgumentFailure {

    public static CommandInputArgument success(Object value) {
        return new ArgumentSuccess(value);
    }

    public static CommandInputArgument fail(ArgumentFailureType type) {
        return new ArgumentFailure(type);
    }

    //

    static final class ArgumentSuccess extends CommandInputArgument {

        final Object value;

        ArgumentSuccess(@Nullable Object value) {
            this.value = value;
        }

    }

    static final class ArgumentFailure extends CommandInputArgument {

        final ArgumentFailureType type;

        ArgumentFailure(@NotNull ArgumentFailureType type) {
            this.type = type;
        }
    }


    public enum ArgumentFailureType {
        MISSING,
        INVALID;
    }
}
