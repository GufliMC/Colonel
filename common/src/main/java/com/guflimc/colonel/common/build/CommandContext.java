package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.dispatch.definition.CommandParameter;
import com.guflimc.colonel.common.dispatch.parser.CommandInput;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import org.jetbrains.annotations.NotNull;

/**
 * This is mostly just a proxy but gives type safety to the {@link #source} method;
 */
public class CommandContext<S> {

    private final ExtCommandContext context;

    public CommandContext(@NotNull ExtCommandContext context) {
        this.context = context;
    }

    public CommandInput input() {
        return context.input();
    }

    public <T> T argument(@NotNull CommandParameter parameter) {
        return context.argument(parameter);
    }

    public <T> T argument(@NotNull String parameter) {
        return context.argument(parameter);
    }

    //

    public S source() {
        return context.source();
    }

    public <T> T source(int index) {
        return context.source(index);
    }

}
