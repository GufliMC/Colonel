package com.gufli.colonel.common.dispatch.definition;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CommandDefinition {

    private final CommandParameter[] parameters;
    private final Map<String, Object> properties;

    public CommandDefinition(@NotNull CommandParameter[] parameters, @NotNull Map<String, Object> properties) {
        this.parameters = parameters;
        this.properties = Collections.unmodifiableMap(properties);

        // check for name validity
        Set<String> names = new HashSet<>();
        for ( CommandParameter parameter : parameters ) {
            if ( !parameter.name().matches("[0-9a-zA-Z_\\-]+") ) {
                throw new IllegalArgumentException("Parameter names must be alphanumeric.");
            }
            if ( names.contains(parameter.name()) ) {
                throw new IllegalArgumentException("Parameter names must be unique. Found duplicate for parameter '" + parameter.name() + "'");
            }

            names.add(parameter.name());
        }

        // check for greedy validity
        for ( int i = 0; i < parameters.length - 1; i++ ) {
            if ( parameters[i].readMode() == ReadMode.GREEDY ) {
                throw new IllegalArgumentException("There can only be one greedy parameter and it must be the last one.");
            }
        }
    }

    public CommandDefinition(@NotNull CommandParameter[] parameters) {
        this(parameters, Collections.emptyMap());
    }

    public CommandParameter[] parameters() {
        return parameters;
    }

    public Map<String, Object> properties() {
        return properties;
    }

    public Optional<Object> property(@NotNull String key) {
        return Optional.ofNullable(properties.get(key));
    }

    public Optional<String> propertyAsString(@NotNull String key) {
        return Optional.ofNullable(properties.get(key)).map(Object::toString);
    }

    //

    @Override
    public String toString() {
        return Arrays.stream(parameters)
                .map(CommandParameter::toString)
                .collect(Collectors.joining(" "));
    }
}
