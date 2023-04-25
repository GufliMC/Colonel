package com.guflimc.colonel.common.parser;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.definition.CommandParameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInputReader {

    private final static Pattern QUOTED = Pattern.compile("^([\"'])((?:\\\\\\1|(?:(?!\\1)).)*)(\\1)");

    private final CommandDefinition definition;

    private int cursor;
    private String buffer;

    public CommandInputReader(CommandDefinition definition, String input, int cursor) {
        this.definition = definition;
        this.cursor = cursor;
        this.buffer = input;
    }

    public CommandInputReader(CommandDefinition definition, String input) {
        this(definition, input, input.length());
    }

    //

    public CommandInput read() {
        CommandInputBuilder builder = CommandInputBuilder.builder();

        int index = 0;
        do {
            // TODO options

            if ( index >= definition.parameters().length ) {
                if ( !buffer.isEmpty() ) {
                    builder.withExcess(buffer);
                }
                break;
            }

            int cc = cursor;

            CommandParameter param = definition.parameters()[index];
            if ( param.parseMode() == CommandParameter.ParseMode.STRING ) {
                builder.success(param, readString());
            } else if ( param.parseMode() == CommandParameter.ParseMode.WORD ) {
                builder.success(param, readWord());
            } else {
                builder.success(param, readAll());
            }

            if ( cc >= 0 && cursor <= 0 ) {
                builder.withCursor(param);
            }

            index++;
        } while ( !buffer.isEmpty() );

        // If cursor = 1 but buffer is empty, the user intended to begin a new parameter but has not entered any input for it yet.
        if ( cursor == 1 && index < definition.parameters().length ) {
            builder.withCursor(definition.parameters()[index]);
            builder.success(definition.parameters()[index], "");
            index++;
        }

        for ( int i = index; i < definition.parameters().length; i++ ) {
            CommandParameter param = definition.parameters()[i];
            builder.fail(param, CommandInputArgument.ArgumentFailureType.MISSING);
        }

        return builder.build();
    }

    //

    private String peek() {
        return buffer;
    }

    private String readWord() {
        int index = buffer.indexOf(" ");

        // return until end
        if (index == -1) {
            String result = buffer;
            skip(result.length());
            return result;
        }

        // return until space
        String result = buffer.substring(0, index);
        skip(index + 1); // also skip space
        return result;
    }

    private String readString() {
        if (buffer.length() == 0)
            throw new IllegalStateException("Cannot continue when there is no data.");

        // first try quotes
        Matcher mr = QUOTED.matcher(buffer);
        if (mr.find()) {
            if (mr.end() != buffer.length() && buffer.charAt(mr.end()) != ' ')
                throw new IllegalArgumentException("The given data is malformed.");

            skip(mr.end() + 1); // also skip space
            return mr.group(2);
        }

        return readWord();
    }

    private String readAll() {
        String result = buffer;
        skip(buffer.length());
        return result;
    }

    private void skip(int amount) {
        if ( amount >= buffer.length() ) {
            cursor -= buffer.length();
            buffer = "";
        } else {
            cursor -= amount;
            buffer = buffer.substring(amount);
        }
    }

}
