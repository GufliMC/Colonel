package com.guflimc.colonel.common.dispatch.tree;

import com.guflimc.colonel.common.ext.ExtCommandContext;
import com.guflimc.colonel.common.dispatch.parser.CommandInputReader;
import com.guflimc.colonel.common.dispatch.parser.CommandInput;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;

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
            CommandInput ci = reader.read();
            min = Math.min(min, ci.errors().size());
            parsed.put(handler, ci);
        }

        // remove handlers with errors from possible targets
        for ( CommandHandler handler : new HashSet<>(parsed.keySet())) {
            if ( parsed.get(handler).errors().size() > min )  // exceeds max error count
                parsed.remove(handler);
            else if ( parsed.get(handler).excess() != null )  // not a match (too many arguments)
                parsed.remove(handler);
        }

        // execute the best handler
        CommandDelegate best = null;
        for (CommandHandler handler : parsed.keySet() ) {
            CommandDelegate delegate = handler.prepare(source, parsed.get(handler));
            ExtCommandContext context = delegate.context();
            if ( context.input().errors().size() == 0 ) {
                delegate.run(); // should actually execute
                return true;
            }

            if ( best == null || best.context().input().errors().size() > context.input().errors().size() )
                best = delegate;
        }

        if ( best != null ) {
            best.run(); // should provide information about errors
            return true;
        }

        return false;
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
            CommandInput ci = reader.read();
            suggestions.addAll(handler.suggestions(source, ci));
        }

        return suggestions;
    }

}
