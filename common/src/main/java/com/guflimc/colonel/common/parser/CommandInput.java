package com.guflimc.colonel.common.parser;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CommandInput {

    private final Map<String, Object> arguments;
    private final Map<String, ParseError> errors;
    private final Map<String, Object> options;

    public CommandInput(@NotNull Map<String, Object> arguments, @NotNull Map<String, ParseError> errors, @NotNull Map<String, Object> options) {
        this.arguments = Map.copyOf(arguments);
        this.errors = Map.copyOf(errors);
        this.options = Map.copyOf(options);
    }

    public CommandInput(@NotNull Map<String, Object> arguments, @NotNull Map<String, ParseError> errors) {
        this(arguments, errors, Map.of());
    }

    public int arguments() {
        return arguments.size();
    }

    public int errors() {
        return errors.size();
    }

    public ParseError error(String name) {
        return errors.get(name);
    }

    public Object argument(String name) {
        return arguments.get(name);
    }

    public Object option(String name) {
        return options.get(name);
    }

    //

    public enum ParseError {
        MISSING,
        INVALID;
    }

}
