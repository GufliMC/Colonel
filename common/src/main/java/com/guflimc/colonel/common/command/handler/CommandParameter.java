package com.guflimc.colonel.common.command.handler;

import com.guflimc.colonel.common.command.CommandContext;

import java.util.*;

public abstract class CommandParameter<T> implements CommandParameterParser<T> {

    private final String name;
    private final Class<T> type;

    private final Collection<CommandParameter<?>> dependencies = new HashSet<>();

    public CommandParameter(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public CommandParameter(String name, Class<T> type, Collection<CommandParameter<?>> dependencies) {
        this.name = name;
        this.type = type;
        this.dependencies.addAll(dependencies);
    }

    public String name() {
        return name;
    }

    public Class<T> type() {
        return type;
    }

    public Collection<CommandParameter<?>> dependencies() {
        return Collections.unmodifiableCollection(dependencies);
    }

    //

    @Override
    public abstract T parse(CommandContext context, String input);

}
