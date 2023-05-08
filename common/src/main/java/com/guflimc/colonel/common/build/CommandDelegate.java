package com.guflimc.colonel.common.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandDelegate implements com.guflimc.colonel.common.dispatch.tree.CommandDelegate {

    private final CommandContext context;
    private final CommandExecutor executor;
    private final Runnable failure;

    CommandDelegate(@NotNull CommandContext context, @NotNull CommandExecutor executor, @Nullable Runnable failure) {
        this.context = context;
        this.executor = executor;
        this.failure = failure;
    }

    @Override
    public CommandContext context() {
        return context;
    }

    @Override
    public void run() {
        if ( failure != null ) {
            failure.run();
            return;
        }

        executor.execute(context);
    }
}
