package com.guflimc.colonel.common.tree;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.parser.CommandInput;
import com.guflimc.colonel.common.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandHandler {

    private final CommandDefinition definition;

    public CommandHandler(@NotNull CommandDefinition definition) {
        this.definition = definition;
    }

    public final CommandDefinition definition() {
        return definition;
    }

    /**
     * @param source The source of the command
     * @param input The input parsed as strings
     * @return List of suggestions for the next missing or partial parameter
     */
    public List<Suggestion> suggestions(Object source, CommandInput input) {
        return List.of();
    }

    /**
     * @param source The source of the command
     * @param input The input parsed as strings
     * @return The delegate which can execute the command or provide information about errors
     */
    public abstract CommandDelegate prepare(Object source, CommandInput input);

    /**
     * @param source The source of the command
     * @return Whether this handler is available or not for the given source.
     */
    public boolean available(Object source) {
        return true;
    }
}
