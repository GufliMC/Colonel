package com.guflimc.colonel.common.command;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    private final static Pattern QUOTED = Pattern.compile("^([\"'])((?:\\\\\\1|(?:(?!\\1)).)*)(\\1)");

    private String input;

    private CommandParser(String input) {
        this.input = input;
    }

    public static CommandParser of(@NotNull String input) {
        return new CommandParser(input);
    }

    //

    public String[] parse() {
        List<String> result = new ArrayList<>();

        while (input.length() > 0) {
            result.add(next());
            if (input.length() >= 1) {
                input = input.substring(1);
            }
        }

        return result.toArray(String[]::new);
    }

    private String next() {
        if (input.length() == 0)
            throw new IllegalStateException("Cannot continue when there is no remaining input.");

        // first try quotes
        Matcher mr = QUOTED.matcher(input);
        if (mr.find()) {
            if (mr.end() != input.length() && input.charAt(mr.end()) != ' ')
                throw new IllegalArgumentException("The given input is malformed.");

            input = input.substring(mr.end());
            return mr.group(2);
        }

        int index = input.indexOf(" ");

        // return everything
        if (index == -1) {
            String result = input;
            input = "";
            return result;
        }

        // return until character
        String result = input.substring(0, index);
        input = input.substring(index);
        return result;
    }

}