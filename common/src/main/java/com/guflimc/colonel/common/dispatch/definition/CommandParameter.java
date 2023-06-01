package com.guflimc.colonel.common.dispatch.definition;

import org.jetbrains.annotations.NotNull;

public class CommandParameter {

    private final String name;
    private final ReadMode readMode;

    public CommandParameter(@NotNull String name, @NotNull ReadMode readMode) {
        this.name = name;
        this.readMode = readMode;
    }

    public CommandParameter(@NotNull String name) {
        this(name, ReadMode.STRING);
    }

    public String name() {
        return name;
    }

    public ReadMode readMode() {
        return readMode;
    }

    //

    @Override
    public String toString() {
        return "<" + name + ">";
    }


    //



}
