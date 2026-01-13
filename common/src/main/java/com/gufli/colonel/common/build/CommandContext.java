package com.gufli.colonel.common.build;

import com.gufli.colonel.common.dispatch.definition.CommandParameter;
import com.gufli.colonel.common.dispatch.parser.CommandInput;
import org.jetbrains.annotations.NotNull;

public class CommandContext {

    private final CommandInput input;

    private final Object source;
    private final Object[] sources;

    public CommandContext(@NotNull CommandInput input, @NotNull Object source, @NotNull Object[] sources) {
        this.input = input;
        this.source = source;
        this.sources = sources;
    }

    public CommandInput input() {
        return input;
    }

    public <T> T argument(@NotNull CommandParameter parameter) {
        return (T) input.argument(parameter);
    }

    public <T> T argument(@NotNull String parameter) {
        return (T) input.argument(parameter);
    }

    //

    public <T> T source() {
        return (T) source;
    }

    public <T> T source(int index) {
        return (T) sources[index];
    }

}
