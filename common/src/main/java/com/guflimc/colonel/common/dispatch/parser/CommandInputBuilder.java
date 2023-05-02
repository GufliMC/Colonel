package com.guflimc.colonel.common.dispatch.parser;

import com.guflimc.colonel.common.dispatch.definition.CommandParameter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandInputBuilder {

    private final Map<CommandParameter, CommandInputArgument> arguments = new HashMap<>();

    private CommandParameter cursor;
    private String excess;

    private CommandInputBuilder() {
    }

    public static CommandInputBuilder builder() {
        return new CommandInputBuilder();
    }

    //

    public CommandInputBuilder fail(@NotNull CommandParameter parameter, @NotNull CommandInputArgument.ArgumentFailureType type) {
        arguments.put(parameter, CommandInputArgument.fail(type));
        return this;
    }

    public CommandInputBuilder success(@NotNull CommandParameter  parameter, Object value) {
        arguments.put(parameter, CommandInputArgument.success(value));
        return this;
    }

    //

    public CommandInputBuilder withCursor(CommandParameter cursor) {
        this.cursor = cursor;
        return this;
    }

    public CommandInputBuilder withExcess(String excess) {
        this.excess = excess;
        return this;
    }

    //

    public CommandInput build() {
        return new CommandInput(arguments, cursor, excess);
    }
}
