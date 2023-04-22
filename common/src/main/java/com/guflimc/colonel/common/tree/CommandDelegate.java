package com.guflimc.colonel.common.tree;

import com.guflimc.colonel.common.parser.CommandInput;
import org.jetbrains.annotations.NotNull;

public abstract class CommandDelegate implements Runnable {

    protected final CommandInput input;

    protected CommandDelegate(@NotNull CommandInput input) {
        this.input = input;
    }

    public final CommandInput input() {
        return input;
    }

}
