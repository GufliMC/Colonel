package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.suggestion.Suggestion;

import java.util.List;

@FunctionalInterface
public interface CommandCompleter<S> {

    List<Suggestion> execute(CommandContext<S> context);

}
