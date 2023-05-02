package com.guflimc.colonel.common.build.parameter;

import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.ext.Argument;
import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.ext.ExtCommandParameter;

import java.util.List;
import java.util.stream.Stream;

public class BooleanParameter extends ExtCommandParameter {

    public BooleanParameter(String name) {
        super(name);
    }

    //

    @Override
    public Argument parse(ExtCommandContext context, String input) {
        if (input.equalsIgnoreCase("true") || input.equals("1")
                || input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
            return Argument.success(true);
        }
        if (input.equalsIgnoreCase("false") || input.equals("0")
                || input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
            return Argument.success(false);
        }
        return Argument.fail(() -> {
            throw new IllegalArgumentException("Invalid boolean value: " + input);
        });
    }

    @Override
    public List<Suggestion> suggestions(ExtCommandContext context, String input) {
        return Stream.of("true", "false")
                .filter(s -> s.startsWith(input.toLowerCase()))
                .map(Suggestion::new).toList();
    }

}
