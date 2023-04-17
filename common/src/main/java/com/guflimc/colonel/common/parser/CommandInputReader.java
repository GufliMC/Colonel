package com.guflimc.colonel.common.parser;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.definition.CommandParameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInputReader {

    private final static Pattern QUOTED = Pattern.compile("^([\"'])((?:\\\\\\1|(?:(?!\\1)).)*)(\\1)");

    private final CommandDefinition definition;
    private String input;

    public CommandInputReader(CommandDefinition definition, String input) {
        this.definition = definition;
        this.input = input;
    }

    //

    public CommandInput read() {
        CommandInputBuilder builder = CommandInputBuilder.of();

        int index = 0;
        while ( !input.isEmpty() ) {
            // TODO options

            CommandParameter param = definition.parameters()[index];
            if ( param.parseMode() == CommandParameter.ParseMode.STRING ) {
                builder.withArgument(param.name(), readString());
            } else if ( param.parseMode() == CommandParameter.ParseMode.WORD ) {
                builder.withArgument(param.name(), readWord());
            } else {
                if ( definition.parameters().length > index + 1 )
                    throw new IllegalStateException("Greedy parameter must be the last one.");
                builder.withArgument(param.name(), readAll());
            }

            index++;
        }

        for ( int i = index; i < definition.parameters().length; i++ ) {
            CommandParameter param = definition.parameters()[i];
            builder.withError(param.name(), CommandInput.ParseError.MISSING);
        }

        return builder.build();
    }

    //

    private String peek() {
        return input;
    }

    private String readWord() {
        int index = input.indexOf(" ");

        // return until end
        if (index == -1) {
            String result = input;
            input = "";
            return result;
        }

        // return until space
        String result = input.substring(0, index);
        input = input.substring(index);
        return result;
    }

    private String readString() {
        if (input.length() == 0)
            throw new IllegalStateException("Cannot continue when there is no data.");

        // first try quotes
        Matcher mr = QUOTED.matcher(input);
        if (mr.find()) {
            if (mr.end() != input.length() && input.charAt(mr.end()) != ' ')
                throw new IllegalArgumentException("The given data is malformed.");

            input = input.substring(mr.end());
            return mr.group(2);
        }

        return readWord();
    }

    private String readAll() {
        String result = input;
        input = "";
        return result;
    }

    private void skip(int amount) {
        if ( amount >= input.length() ) {
            input = "";
        } else {
            input = input.substring(amount);
        }
    }

}
