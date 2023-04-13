package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.handler.CommandHandler;
import com.guflimc.colonel.common.command.handler.CommandParameter;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandContext implements CommandSourceContext {

    private final Command command;
    private final CommandHandler handler;

    final Map<CommandParameter<?>, Object> parsed = new HashMap<>();

    public CommandContext(Command command, CommandHandler handler) {
        this.command = command;
        this.handler = handler;
    }

    @Override
    public Command command() {
        return command;
    }

    @Override
    public CommandHandler handler() {
        return handler;
    }

    //

    @Override
    public <T> T source() {
        return (T) command.source();
    }

    public abstract <T> T source(Class<T> type);

    public abstract <T> T source(String providerName);

    //

    public <T> T get(CommandParameter<T> parameter) {
        return parameter.type().cast(parsed.get(parameter));
    }

    public <T> T get(String parameterName) {
        return parsed.keySet().stream().filter(key -> key.name().equals(parameterName))
                .map(key -> (CommandParameter<T>) key).map(this::get).findFirst().orElse(null);
    }

}
