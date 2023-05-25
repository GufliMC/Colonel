package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.exception.CommandExecutionFailure;
import com.guflimc.colonel.common.exception.CommandFailure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandDelegate implements com.guflimc.colonel.common.dispatch.tree.CommandDelegate {

    private final CommandContext context;
    private final CommandExecutor executor;
    private final CommandFailure failure;

    CommandDelegate(@NotNull CommandContext context, @NotNull CommandExecutor executor, @Nullable CommandFailure failure) {
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
        if (failure != null) {
            throw failure;
        }

        try {
            executor.execute(context);
        } catch (Throwable t) {
            throw new CommandExecutionFailure(t);
        }
    }
}
