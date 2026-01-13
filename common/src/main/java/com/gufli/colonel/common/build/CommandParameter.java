package com.gufli.colonel.common.build;

import com.gufli.colonel.common.dispatch.definition.ReadMode;
import com.gufli.colonel.common.dispatch.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class CommandParameter extends com.gufli.colonel.common.dispatch.definition.CommandParameter implements CommandParameterParser, CommandParameterCompleter {

    public CommandParameter(@NotNull String name, @NotNull ReadMode readMode) {
        super(name, readMode);
    }

    public CommandParameter(@NotNull String name) {
        super(name);
    }

    @Override
    public List<Suggestion> suggestions(CommandContext context, String input) throws Throwable {
        return List.of();
    }

    //

    public static CommandParameter of(@NotNull String name,
                                      @NotNull ReadMode readMode,
                                      @NotNull CommandParameterParser parser,
                                      @NotNull CommandParameterCompleter completer) {
        return new ExtcommandParameterImpl(name, readMode, parser, completer);
    }

    private static class ExtcommandParameterImpl extends CommandParameter {

        private final CommandParameterParser parser;
        private final CommandParameterCompleter completer;

        public ExtcommandParameterImpl(@NotNull String name,
                                   @NotNull ReadMode readMode,
                                   @NotNull CommandParameterParser parser,
                                   @NotNull CommandParameterCompleter completer) {
            super(name, readMode);
            this.parser = parser;
            this.completer = completer;
        }

        @Override
        public Object parse(CommandContext context, String input) throws Throwable {
            return parser.parse(context, input);
        }

        @Override
        public List<Suggestion> suggestions(CommandContext context, String input) throws Throwable {
            return completer.suggestions(context, input);
        }

    }
}
