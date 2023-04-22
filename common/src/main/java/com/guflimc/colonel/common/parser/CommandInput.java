package com.guflimc.colonel.common.parser;

import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.parser.Parser;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

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

    public ParseError error(String name) {
        return errors.get(name);
    }

    public Collection<String> errors() {
        return errors.keySet();
    }

    public Collection<String> errors(ParseError type) {
        return errors.entrySet().stream()
                .filter(e -> e.getValue() == type)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    //

    public Object argument(String name) {
        return arguments.get(name);
    }

    public Collection<String> arguments() {
        return arguments.keySet();
    }

    //

    public Object option(String name) {
        return options.get(name);
    }

    public Collection<String> options() {
        return arguments.keySet();
    }

    //

    public enum ParseError {
        MISSING,
        INVALID;
    }

}
