package com.gufli.colonel.common.exception;

/**
 * This error likely indicates a bug introduced by the developer. Information should not be displayed to the end user.
 */
public class CommandPrepareSourceFailure extends CommandHandleFailure {

    private int sourceMapperIndex;

    public CommandPrepareSourceFailure(Throwable cause) {
        super(cause);
    }

    public CommandPrepareSourceFailure withSourceMapperIndex(int sourceMapperIndex) {
        this.sourceMapperIndex = sourceMapperIndex;
        return this;
    }

    public int sourceMapperIndex() {
        return sourceMapperIndex;
    }

}
