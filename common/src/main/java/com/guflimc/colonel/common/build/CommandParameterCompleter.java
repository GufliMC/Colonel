package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;

import java.util.List;

@FunctionalInterface
public interface CommandParameterCompleter {

    List<Suggestion> suggestions(CommandContext context, String input);

}
