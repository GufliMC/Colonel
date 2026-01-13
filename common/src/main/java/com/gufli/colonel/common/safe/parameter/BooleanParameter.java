package com.gufli.colonel.common.safe.parameter;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.build.CommandParameter;
import com.gufli.colonel.common.dispatch.suggestion.Suggestion;

import java.util.List;
import java.util.stream.Stream;

public class BooleanParameter extends CommandParameter {

    public BooleanParameter(String name) {
        super(name);
    }

    //

    @Override
    public Object parse(CommandContext context, String input) {
        if (input.equalsIgnoreCase("true") || input.equals("1")
                || input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
            return true;
        }
        if (input.equalsIgnoreCase("false") || input.equals("0")
                || input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
            return false;
        }
        throw new IllegalArgumentException("Invalid boolean value: " + input);
    }

    @Override
    public List<Suggestion> suggestions(CommandContext context, String input) {
        return Stream.of("true", "false")
                .filter(s -> s.startsWith(input.toLowerCase()))
                .map(Suggestion::new).toList();
    }

}
