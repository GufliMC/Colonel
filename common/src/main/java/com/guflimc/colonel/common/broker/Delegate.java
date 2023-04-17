package com.guflimc.colonel.common.broker;

import com.guflimc.colonel.common.parser.CommandInput;
import org.jetbrains.annotations.NotNull;

public abstract class Delegate implements Runnable {

    private final CommandInput input;

    protected Delegate(@NotNull CommandInput input) {
        this.input = input;
    }

    public final CommandInput input() {
        return input;
    }

}
