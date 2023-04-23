package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.suggestion.Suggestion;

import java.util.List;

@FunctionalInterface
public interface CommandParameterCompleter<S> {

    List<Suggestion> suggestions(CommandContext<S> context, String input);

}
