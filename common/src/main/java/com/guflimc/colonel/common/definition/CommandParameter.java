package com.guflimc.colonel.common.definition;

public class CommandParameter {

    private final String name;
    private final ParseMode parseMode;

    public CommandParameter(String name, ParseMode parseMode) {
        this.name = name;
        this.parseMode = parseMode;
    }

    public CommandParameter(String name) {
        this(name, ParseMode.STRING);
    }

    public String name() {
        return name;
    }

    public ParseMode parseMode() {
        return parseMode;
    }

    //

    public enum ParseMode {
        /** Read a single word. **/
        WORD,
        /** Read a single word or a string with spaces if it is quoted. **/
        STRING,
        /** Read the remaining input. **/
        GREEDY;
    }
}
