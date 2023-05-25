package com.guflimc.colonel.common.exception;

/**
 * This error likely indicates invalid input from the end user. The cause might also indicate a bug introduced by the developer.
 */
public class CommandPrepareParameterFailure extends CommandParameterFailure {

    public CommandPrepareParameterFailure(Throwable cause) {
        super(cause);
    }
}
