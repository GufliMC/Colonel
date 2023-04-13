package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.syntax.CommandSyntax;
import com.guflimc.colonel.common.command.syntax.CommandParameter;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandContext implements CommandSourceContext {

    private final Command command;
    private final CommandSyntax syntax;

    final Map<CommandParameter<?>, Object> parsed = new HashMap<>();

    public CommandContext(Command command, CommandSyntax syntax) {
        this.command = command;
        this.syntax = syntax;
    }

    @Override
    public Command command() {
        return command;
    }

    @Override
    public CommandSyntax syntax() {
        return syntax;
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
        return parameter.type().type().cast(parsed.get(parameter));
    }

    public <T> T get(String parameterName) {
        return parsed.keySet().stream().filter(key -> key.name().equals(parameterName))
                .map(key -> (CommandParameter<T>) key).map(this::get).findFirst().orElse(null);
    }

}
