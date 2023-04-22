package com.guflimc.colonel.common.build;

@FunctionalInterface
public interface CommandExecutor<S> {

    void execute(CommandContext<S> context);

}
