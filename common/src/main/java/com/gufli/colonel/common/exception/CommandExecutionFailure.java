package com.gufli.colonel.common.exception;

/**
 * This error likely indicates a bug introduced by the developer. Information should not be displayed to the end user.
 */
public class CommandExecutionFailure extends CommandHandleFailure {

    public CommandExecutionFailure(Throwable cause) {
        super(cause);
    }

}
