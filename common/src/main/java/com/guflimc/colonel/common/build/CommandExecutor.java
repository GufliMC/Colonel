package com.guflimc.colonel.common.build;

@FunctionalInterface
public interface CommandExecutor {

    void execute(CommandContext context) throws Throwable;

}
