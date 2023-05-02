package com.guflimc.colonel.common.ext;

import com.guflimc.colonel.common.dispatch.definition.CommandParameter;
import com.guflimc.colonel.common.dispatch.definition.ReadMode;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ExtCommandParameter extends CommandParameter implements ExtCommandParameterParser, ExtCommandParameterCompleter {

    public ExtCommandParameter(@NotNull String name, @NotNull ReadMode readMode) {
        super(name, readMode);
    }

    public ExtCommandParameter(@NotNull String name) {
        super(name);
    }

    @Override
    public List<Suggestion> suggestions(ExtCommandContext context, String input) {
        return List.of();
    }

    //

    public static ExtCommandParameter of(@NotNull String name,
                                         @NotNull ReadMode readMode,
                                         @NotNull ExtCommandParameterParser parser,
                                         @NotNull ExtCommandParameterCompleter completer) {
        return new ExtcommandParameterImpl(name, readMode, parser, completer);
    }

    private static class ExtcommandParameterImpl extends ExtCommandParameter {

        private final ExtCommandParameterParser parser;
        private final ExtCommandParameterCompleter completer;

        public ExtcommandParameterImpl(@NotNull String name,
                                   @NotNull ReadMode readMode,
                                   @NotNull ExtCommandParameterParser parser,
                                   @NotNull ExtCommandParameterCompleter completer) {
            super(name, readMode);
            this.parser = parser;
            this.completer = completer;
        }

        @Override
        public Argument parse(ExtCommandContext context, String input) {
            return parser.parse(context, input);
        }

        @Override
        public List<Suggestion> suggestions(ExtCommandContext context, String input) {
            return completer.suggestions(context, input);
        }

    }
}
