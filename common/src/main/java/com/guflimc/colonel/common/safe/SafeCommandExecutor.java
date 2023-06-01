package com.guflimc.colonel.common.safe;

@FunctionalInterface
public interface SafeCommandExecutor<S> {

    void execute(SafeCommandContext<S> context) throws Throwable;

}
