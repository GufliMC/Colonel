package com.gufli.colonel.common.safe;

import com.gufli.colonel.common.dispatch.definition.CommandParameter;
import com.gufli.colonel.common.dispatch.parser.CommandInput;
import com.gufli.colonel.common.build.CommandContext;
import org.jetbrains.annotations.NotNull;

/**
 * This is mostly just a proxy but gives stricter type safety to the {@link #source} method;
 */
public class SafeCommandContext<S> {

    private final CommandContext context;

    public SafeCommandContext(@NotNull CommandContext context) {
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
