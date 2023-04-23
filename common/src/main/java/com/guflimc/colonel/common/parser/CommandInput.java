package com.guflimc.colonel.common.parser;

import com.guflimc.colonel.common.definition.CommandParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandInput {

    private final Map<CommandParameter, CommandInputArgument> arguments;

    private final CommandParameter cursor;
    private final String excess;

    public CommandInput(@NotNull Map<CommandParameter, CommandInputArgument> arguments,
                        @Nullable CommandParameter cursor,
                        @Nullable String excess) {
        this.arguments = Map.copyOf(arguments);
        this.cursor = cursor;
        this.excess = excess;
    }

    public CommandInput(@NotNull Map<CommandParameter, CommandInputArgument> arguments) {
        this(arguments, null, null);
    }

    //

    public CommandParameter cursor() {
        return cursor;
    }

    public String excess() {
        return excess;
    }

    //

    public boolean failure(CommandParameter parameter) {
        return arguments.get(parameter) instanceof CommandInputArgument.ArgumentFailure;
    }

    public CommandInputArgument.ArgumentFailureType error(CommandParameter parameter) {
        if ( arguments.get(parameter) instanceof CommandInputArgument.ArgumentFailure ) {
            return ((CommandInputArgument.ArgumentFailure) arguments.get(parameter)).type;
        }
        return null;
    }

    public Collection<CommandParameter> errors() {
        return arguments.entrySet().stream()
                .filter(e -> e.getValue() instanceof CommandInputArgument.ArgumentFailure)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Collection<CommandParameter> errors(CommandInputArgument.ArgumentFailureType type) {
        return arguments.entrySet().stream()
                .filter(e -> e.getValue() instanceof CommandInputArgument.ArgumentFailure)
                .filter(e -> ((CommandInputArgument.ArgumentFailure) e.getValue()).type == type)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    //

    public boolean success(CommandParameter parameter) {
        return arguments.get(parameter) instanceof CommandInputArgument.ArgumentSuccess;
    }

    public Object argument(CommandParameter parameter) {
        if ( arguments.get(parameter) instanceof CommandInputArgument.ArgumentSuccess ) {
            return ((CommandInputArgument.ArgumentSuccess) arguments.get(parameter)).value;
        }
        return null;
    }

    public Collection<CommandParameter> arguments() {
        return arguments.entrySet().stream()
                .filter(e -> e.getValue() instanceof CommandInputArgument.ArgumentSuccess)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }


}
