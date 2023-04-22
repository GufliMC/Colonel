package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.definition.CommandParameter;
import com.guflimc.colonel.common.tree.CommandHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandHandlerBuilder<S> {

    private final Map<CommandParameter, Function<String, Object>> parameters = new LinkedHashMap<>();
    private CommandExecutor<S> executor;

    //

    public static <S> CommandHandlerBuilder<S> builder() {
        return new CommandHandlerBuilder<>();
    }

    //

    public CommandHandlerBuilder<S> word(String name, Function<String, Object> parser) {
        parameters.put(new CommandParameter(name, CommandParameter.ParseMode.WORD), parser);
        return this;
    }

    public CommandHandlerBuilder<S> string(String name, Function<String, Object> parser) {
        parameters.put(new CommandParameter(name, CommandParameter.ParseMode.STRING), parser);
        return this;
    }

    public CommandHandlerBuilder<S> greedy(String name, Function<String, Object> parser) {
        parameters.put(new CommandParameter(name, CommandParameter.ParseMode.GREEDY), parser);
        return this;
    }

    //

    public CommandHandlerBuilder<S> executor(CommandExecutor<S> executor) {
        this.executor = executor;
        return this;
    }

    //

    public CommandHandler build() {
        return new CommandHandlerImpl<>(parameters, executor);
    }
}
