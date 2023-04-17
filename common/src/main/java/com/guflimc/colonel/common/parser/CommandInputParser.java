package com.guflimc.colonel.common.parser;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.definition.CommandParameter;

public abstract class CommandInputParser {

    private final CommandDefinition definition;
    private final CommandInput input;

    public CommandInputParser(CommandDefinition definition, CommandInput input) {
        this.definition = definition;
        this.input = input;
    }

    //

    public CommandInput parse() {
        CommandInputBuilder builder = CommandInputBuilder.of();

        for (CommandParameter param : definition.parameters()) {
            CommandInput.ParseError error = input.error(param.name());
            if ( error != null ) {
                builder.withError(param.name(), error);
                continue;
            }

            Object value = parse(param, (String) input.argument(param.name()));
            if ( value == null ) {
                builder.withError(param.name(), CommandInput.ParseError.INVALID);
                continue;
            }

            builder.withArgument(param.name(), value);
        }

        return builder.build();
    }

    //

    public abstract Object parse(CommandParameter param, String value);
}
