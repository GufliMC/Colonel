package com.guflimc.colonel.common.parser;

import java.util.HashMap;
import java.util.Map;

public class CommandInputBuilder {

    private final Map<String, Object> arguments = new HashMap<>();
    private final Map<String, CommandInput.ParseError> errors = new HashMap<>();
    private final Map<String, Object> options = new HashMap<>();

    private CommandInputBuilder() {
    }

    public static CommandInputBuilder of() {
        return new CommandInputBuilder();
    }

    //

    public CommandInputBuilder withError(String parameter, CommandInput.ParseError error) {
        errors.put(parameter, error);
        return this;
    }

    public CommandInputBuilder withArgument(String parameter, Object value) {
        arguments.put(parameter, value);
        return this;
    }

    public CommandInputBuilder withOption(String parameter, Object value) {
        options.put(parameter, value);
        return this;
    }

    //

    public CommandInput build() {
        return new CommandInput(arguments, errors, options);
    }
}
