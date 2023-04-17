package com.guflimc.colonel.common.broker;

import com.guflimc.colonel.common.parser.CommandInput;
import com.guflimc.colonel.common.parser.CommandInputReader;
import com.guflimc.colonel.common.suggestion.Suggestion;

import java.util.*;

public final class Mediator {

    private final List<Handler> handlers = new ArrayList<>();

    public void addHandler(Handler handler) {
        handlers.add(handler);
    }

    //

    public boolean apply(Object source, String input) {
        // parse arguments in as strings
        Map<Handler, CommandInput> parsed = new LinkedHashMap<>(); // keep order
        int min = Integer.MAX_VALUE;
        for (Handler handler : handlers) {
            CommandInputReader reader = new CommandInputReader(handler.definition(), input);
            CommandInput ci = reader.read();
            min = Math.min(min, ci.errors());
        }

        for ( Handler handler : handlers ) {
            if ( parsed.get(handler).errors() > min )
                parsed.remove(handler);
        }

        // execute the best handler
        Delegate best = null;
        for (Handler handler : parsed.keySet() ) {
            Delegate delegate = handler.prepare(source, parsed.get(handler));
            if ( delegate.input().errors() == 0 ) {
                delegate.run(); // should actually execute
                return true;
            }

            if ( best == null || best.input().errors() > delegate.input().errors() )
                best = delegate;
        }

        if ( best != null ) {
            best.run(); // should provide information about errors
            return true;
        }

        return false;
    }

    public List<Suggestion> suggestions(Object source, String input) {
        List<Suggestion> suggestions = new ArrayList<>();

        // parse arguments in as strings and ask suggestions
        for (Handler handler : handlers) {
            CommandInputReader reader = new CommandInputReader(handler.definition(), input);
            CommandInput ci = reader.read();
            suggestions.addAll(handler.suggestions(source, ci));
        }

        return suggestions;
    }
}
