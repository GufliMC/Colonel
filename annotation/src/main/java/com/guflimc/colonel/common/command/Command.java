package com.guflimc.colonel.common.command;

public class Command {

    private final Object source;
    private final String[] input;

    public Command(Object source, String[] input) {
        this.source = source;
        this.input = input;
    }

    public Object source() {
        return source;
    }

    public String[] input() {
        return input;
    }
}
