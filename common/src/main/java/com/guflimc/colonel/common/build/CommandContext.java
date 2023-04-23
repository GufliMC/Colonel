package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.definition.CommandParameter;
import com.guflimc.colonel.common.parser.CommandInput;

import java.util.Arrays;

public class CommandContext<S> {

    private final CommandDefinition definition;
    private final S source;
    final CommandInput input;

    public CommandContext(CommandDefinition definition, S source, CommandInput input) {
        this.definition = definition;
        this.source = source;
        this.input = input;
    }

    public S source() {
        return source;
    }

    public <T> T argument(CommandParameter parameter) {
        return (T) input.argument(parameter);
    }

    public <T> T argument(String parameter) {
        return Arrays.stream(definition.parameters()).filter(p -> p.name().equals(parameter))
                .findFirst().map(param -> (T) argument(param)).orElse(null);
    }

}
