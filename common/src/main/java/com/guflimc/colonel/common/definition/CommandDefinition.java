package com.guflimc.colonel.common.definition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandDefinition {

    private final CommandParameter[] parameters;

    public CommandDefinition(CommandParameter[] parameters) {
        this.parameters = parameters;

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
            if ( parameters[i].readMode() == CommandParameter.ReadMode.GREEDY ) {
                throw new IllegalArgumentException("There can only be one greedy parameter and it must be the last one.");
            }
        }
    }

    public CommandParameter[] parameters() {
        return parameters;
    }

    //

    @Override
    public String toString() {
        return Arrays.stream(parameters)
                .map(CommandParameter::toString)
                .collect(Collectors.joining(" "));
    }
}
