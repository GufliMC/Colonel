package com.guflimc.colonel.common.safe;

import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface SafeCommandParameterCompleter<S> {

    List<Suggestion> suggestions(SafeCommandContext<S> context, String input) throws Exception;

    static <S> SafeCommandParameterCompleter<S> withMatchCheck(@NotNull SafeCommandParameterCompleter<S> completer) {
        return (context, input) -> {
            String lc = input.toLowerCase();
            return completer.suggestions(context, input).stream()
                    .filter(suggestion -> suggestion.value().toLowerCase().startsWith(lc))
                    .toList();
        };
    }

}
