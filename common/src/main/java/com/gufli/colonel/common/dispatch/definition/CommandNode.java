package com.gufli.colonel.common.dispatch.definition;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class CommandNode {

    private final String name;
    private final Collection<CommandDefinition> definitions;
    private final Collection<CommandNode> children;

    public CommandNode(@NotNull String name, @NotNull Collection<CommandDefinition> definitions, Collection<CommandNode> children) {
        this.name = name;
        this.definitions = Collections.unmodifiableCollection(definitions);
        this.children = Collections.unmodifiableCollection(children);
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull Collection<CommandDefinition> definitions() {
        return definitions;
    }

    public @NotNull Collection<CommandNode> children() {
        return children;
    }

}
