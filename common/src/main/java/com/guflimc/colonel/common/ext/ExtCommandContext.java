package com.guflimc.colonel.common.ext;

import com.guflimc.colonel.common.dispatch.definition.CommandParameter;
import com.guflimc.colonel.common.dispatch.parser.CommandInput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ExtCommandContext {

    private final CommandInput input;

    private final Object source;
    private final Supplier<Object>[] sources;

    public ExtCommandContext(@NotNull CommandInput input, @NotNull Object source, @NotNull Supplier<Object>[] sources) {
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
        return (T) sources[index].get();
    }

}
