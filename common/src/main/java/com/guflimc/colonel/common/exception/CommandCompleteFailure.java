package com.guflimc.colonel.common.exception;

/**
 * This error likely indicates a bug introduced by the developer. Information should not be displayed to the end user.
 */
public class CommandCompleteFailure extends CommandParameterFailure {

    public CommandCompleteFailure(Throwable cause) {
        super(cause);
    }
}
