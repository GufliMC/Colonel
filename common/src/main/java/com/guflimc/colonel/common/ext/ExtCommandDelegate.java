package com.guflimc.colonel.common.ext;

import com.guflimc.colonel.common.dispatch.tree.CommandDelegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtCommandDelegate implements CommandDelegate {

    private final ExtCommandContext context;
    private final ExtCommandExecutor executor;
    private final Runnable failure;

    ExtCommandDelegate(@NotNull ExtCommandContext context, @NotNull ExtCommandExecutor executor, @Nullable Runnable failure) {
        this.context = context;
        this.executor = executor;
        this.failure = failure;
    }

    @Override
    public ExtCommandContext context() {
        return context;
    }

    @Override
    public void run() {
        if ( context.input().errors().isEmpty() ) {
            executor.execute(context);
            return;
        }

        if ( failure == null ) {
            return;
        }

        failure.run();
    }
}
