package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.tree.CommandDelegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandDelegateImpl<S> extends CommandDelegate {

    final CommandContext<S> context;

    final CommandExecutor<S> executor;
    final Runnable failure;

    CommandDelegateImpl(@NotNull CommandContext<S> context, @NotNull CommandExecutor<S> executor, @Nullable Runnable failure) {
        super(context.input);
        this.context = context;
        this.executor = executor;
        this.failure = failure;
    }

    @Override
    public void run() {
        if ( input.errors().isEmpty() ) {
            executor.execute(context);
            return;
        }

        if ( failure == null ) {
            return;
        }

        failure.run();
    }
}
