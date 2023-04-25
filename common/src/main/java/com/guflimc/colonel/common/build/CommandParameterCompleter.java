package com.guflimc.colonel.common.build;

import com.guflimc.colonel.common.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface CommandParameterCompleter<S> {

    List<Suggestion> suggestions(CommandContext<S> context, String input);

    static <S> CommandParameterCompleter<S> startsWith(@NotNull CommandParameterCompleter<S> completer) {
        return (context, input) -> {
            String lc = input.toLowerCase();
            return completer.suggestions(context, input).stream()
                    .filter(suggestion -> suggestion.value().toLowerCase().startsWith(lc))
                    .toList();
        };
    }

}
