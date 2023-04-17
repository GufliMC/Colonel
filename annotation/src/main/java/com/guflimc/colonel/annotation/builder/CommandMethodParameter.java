package com.guflimc.colonel.annotation.builder;

import com.guflimc.colonel.common.command.CommandContext;
import com.guflimc.colonel.common.command.syntax.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CommandMethodParameter<T> {

    private final String name;
    private CommandParameterType<T> type;

    public CommandMethodParameter(String name, CommandParameterType<T> type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public CommandParameterType<T> type() {
        return type;
    }

    public void setParser(CommandParameterParser<T> parser) {
        this.type = new CommandParameterType<T>(type.type()) {
            @Override
            public T parse(@NotNull CommandContext context, @NotNull String input) {
                return parser.parse(context, input);
            }

            @Override
            public List<CommandParameterSuggestion> suggest(@NotNull CommandContext context, @NotNull String input) {
                return type.suggest(context, input);
            }
        };
    }

    public void setSuggestionProvider(CommandParameterSuggestionProvider suggestionProvider) {
        this.type = new CommandParameterType<T>(type.type()) {
            @Override
            public T parse(@NotNull CommandContext context, @NotNull String input) {
                return type.parse(context, input);
            }

            @Override
            public List<CommandParameterSuggestion> suggest(@NotNull CommandContext context, @NotNull String input) {
                return suggestionProvider.suggest(context, input);
            }
        };
    }

    public CommandParameter<T> build() {
        return new CommandParameter<>(name, type);
    }
}
