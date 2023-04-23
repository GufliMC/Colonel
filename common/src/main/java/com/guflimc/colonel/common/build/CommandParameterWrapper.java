package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.definition.CommandParameter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandParameterWrapper<S> {

    private final CommandParameter parameter;
    private final CommandParameterParser<S> parser;
    private final CommandParameterCompleter<S> completer;

    public CommandParameterWrapper(@NotNull CommandParameter parameter,
                                   @NotNull CommandParameterParser<S> parser,
                                   @NotNull CommandParameterCompleter<S> completer) {
        this.parameter = parameter;
        this.parser = parser;
        this.completer = completer;
    }

    public CommandParameterWrapper(@NotNull CommandParameter parameter,
                                   @NotNull CommandParameterParser<S> parser) {
        this(parameter, parser, (ctx, str) -> List.of());
    }

    public CommandParameter parameter() {
        return parameter;
    }

    public CommandParameterParser<S> parser() {
        return parser;
    }

    public CommandParameterCompleter<S> completer() {
        return completer;
    }
}
