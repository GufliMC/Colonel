package com.guflimc.colonel.common.definition;

public class CommandParameter {

    private final String name;
    private final ReadMode readMode;

    public CommandParameter(String name, ReadMode readMode) {
        this.name = name;
        this.readMode = readMode;
    }

    public CommandParameter(String name) {
        this(name, ReadMode.STRING);
    }

    public String name() {
        return name;
    }

    public ReadMode parseMode() {
        return readMode;
    }

    //

    public enum ReadMode {
        /** Read a single word. **/
        WORD,
        /** Read a single word or a string with spaces if it is quoted. **/
        STRING,
        /** Read the remaining input. **/
        GREEDY;
    }
}
