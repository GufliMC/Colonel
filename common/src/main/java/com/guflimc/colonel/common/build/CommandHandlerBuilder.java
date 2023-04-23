package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.definition.CommandParameter;
import com.guflimc.colonel.common.tree.CommandHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandHandlerBuilder<S> {

    private final List<CommandParameterWrapper<S>> parameters = new ArrayList<>();
    private CommandExecutor<S> executor;

    //

    public static <S> CommandHandlerBuilder<S> builder() {
        return new CommandHandlerBuilder<>();
    }

    //

    public CommandHandlerBuilder<S> parameter(@NotNull CommandParameter param,
                                              @NotNull CommandParameterParser<S> parser) {
        parameters.add(new CommandParameterWrapper<>(param, parser));
        return this;
    }

    public CommandHandlerBuilder<S> parameter(@NotNull CommandParameter param,
                                              @NotNull CommandParameterParser<S> parser,
                                              @NotNull CommandParameterCompleter<S> completer) {
        parameters.add(new CommandParameterWrapper<>(param, parser, completer));
        return this;
    }

    //

    public CommandHandlerBuilder<S> word(@NotNull String name,
                                         @NotNull CommandParameterParser<S> parser) {
        return parameter(new CommandParameter(name, CommandParameter.ParseMode.WORD), parser);
    }

    public CommandHandlerBuilder<S> word(@NotNull String name,
                                         @NotNull CommandParameterParser<S> parser,
                                         @NotNull CommandParameterCompleter<S> completer) {
        return parameter(new CommandParameter(name, CommandParameter.ParseMode.WORD), parser, completer);
    }

    public CommandHandlerBuilder<S> string(@NotNull String name,
                                           @NotNull CommandParameterParser<S> parser) {
        return parameter(new CommandParameter(name, CommandParameter.ParseMode.STRING), parser);
    }

    public CommandHandlerBuilder<S> string(@NotNull String name,
                                           @NotNull CommandParameterParser<S> parser,
                                           @NotNull CommandParameterCompleter<S> completer) {
        return parameter(new CommandParameter(name, CommandParameter.ParseMode.STRING), parser, completer);
    }

    public CommandHandlerBuilder<S> greedy(@NotNull String name,
                                           @NotNull CommandParameterParser<S> parser) {
        return parameter(new CommandParameter(name, CommandParameter.ParseMode.GREEDY), parser);
    }

    public CommandHandlerBuilder<S> greedy(@NotNull String name,
                                           @NotNull CommandParameterParser<S> parser,
                                           @NotNull CommandParameterCompleter<S> completer) {
        return parameter(new CommandParameter(name, CommandParameter.ParseMode.GREEDY), parser, completer);
    }

    //

    public CommandHandlerBuilder<S> executor(CommandExecutor<S> executor) {
        this.executor = executor;
        return this;
    }

    //

    public CommandHandler build() {
        if ( executor == null ) {
            throw new IllegalStateException("An executor must be provided before a handler can be built.");
        }
        return new CommandHandlerImpl<>(parameters, executor);
    }
}
