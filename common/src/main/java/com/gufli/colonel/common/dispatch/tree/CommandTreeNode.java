package com.gufli.colonel.common.dispatch.tree;

import com.gufli.colonel.common.build.CommandContext;
import com.gufli.colonel.common.dispatch.definition.CommandDefinition;
import com.gufli.colonel.common.dispatch.parser.CommandInputReader;
import com.gufli.colonel.common.dispatch.parser.CommandInput;
import com.gufli.colonel.common.dispatch.suggestion.Suggestion;
import com.gufli.colonel.common.exception.CommandHandleFailure;

import java.util.*;

public final class CommandTreeNode {

    private final String name;

    // yes these are exposed, no unmodifiable wrappers etc.
    private final List<CommandTreeNode> children = new ArrayList<>();
    private final List<CommandHandler> handlers = new ArrayList<>();

    public CommandTreeNode(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public List<CommandTreeNode> children() {
        return children;
    }

    public List<CommandHandler> handlers() {
        return handlers;
    }

    //

    public boolean apply(Object source, String input) {
        // parse arguments in as strings
        Map<CommandHandler, CommandInput> parsed = new LinkedHashMap<>(); // keep order
        int min = Integer.MAX_VALUE;
        for (CommandHandler handler : handlers) {
            if ( !handler.available(source) ) {
                continue;
            }

            CommandInputReader reader = new CommandInputReader(handler.definition(), input);
            CommandInput ci;
            try {
                ci = reader.read();
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            min = Math.min(min, ci.errors().size());
            parsed.put(handler, ci);
        }

        // remove handlers that don't match or have too many errors
        for ( CommandHandler handler : new HashSet<>(parsed.keySet())) {
            if ( parsed.get(handler).errors().size() > min )  // exceeds max error count
                parsed.remove(handler);
            else if ( parsed.get(handler).excess() != null )  // not a match (too many arguments)
                parsed.remove(handler);
        }

        // find the best handler
        CommandDelegate best = null;
        CommandDefinition def = null;

        for (CommandHandler handler : parsed.keySet() ) {
            CommandDelegate delegate = handler.prepare(source, parsed.get(handler));
            CommandContext context = delegate.context();
            if ( context.input().errors().size() == 0 ) {
                best = delegate;
                def = handler.definition();
                break;
            }

            if ( best == null || best.context().input().errors().size() > context.input().errors().size() ) {
                best = delegate;
                def = handler.definition();
            }
        }

        if ( best == null ) {
            return false;
        }

        try {
            best.run(); // executes the command or throws an exception with more information
            return true;
        } catch (CommandHandleFailure ex) {
            throw ex.withDefinition(def);
        }
    }

    public List<Suggestion> suggestions(Object source, String input, int cursor) {
        if ( cursor < 0 ) {
            return List.of();
        }

        List<Suggestion> suggestions = new ArrayList<>();

        // parse arguments in as strings and ask suggestions
        for (CommandHandler handler : handlers) {
            if ( !handler.available(source) ) {
                continue;
            }

            CommandInputReader reader = new CommandInputReader(handler.definition(), input, cursor);
            CommandInput ci;
            try {
                ci = reader.read();
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            try {
                suggestions.addAll(handler.suggestions(source, ci));
            } catch (CommandHandleFailure ex) {
                throw ex.withDefinition(handler.definition());
            }
        }

        return suggestions;
    }

}
