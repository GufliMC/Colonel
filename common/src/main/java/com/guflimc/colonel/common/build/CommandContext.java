package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.parser.CommandInput;

public class CommandContext<S> {

    private final S source;
    private final CommandInput input;

    public CommandContext(S source, CommandInput input) {
        this.source = source;
        this.input = input;
    }

    public S source() {
        return source;
    }

    public <T> T argument(String name) {
        return (T) input.argument(name);
    }

    public <T> T option(String name) {
        return (T) input.option(name);
    }
}
