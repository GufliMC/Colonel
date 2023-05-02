package com.guflimc.colonel.common.ext;

import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;

import java.util.List;

@FunctionalInterface
public interface ExtCommandParameterCompleter {

    List<Suggestion> suggestions(ExtCommandContext context, String input);

}
