package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.syntax.CommandParameter;
import com.guflimc.colonel.common.command.syntax.CommandSyntax;

import java.util.HashMap;
import java.util.Map;

public class CommandContext implements CommandSourceContext {

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

    public <T> T argument(CommandParameter<T> parameter) {
        return parameter.type().type().cast(parsed.get(parameter));
    }

    @SuppressWarnings("unchecked")
    public <T> T argument(String name) {
        return parsed.keySet().stream().filter(key -> key.name().equals(name))
                .map(key -> (CommandParameter<T>) key)
                .map(this::argument)
                .findFirst().orElse(null);
    }

}
