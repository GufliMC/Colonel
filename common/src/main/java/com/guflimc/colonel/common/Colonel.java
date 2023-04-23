package com.guflimc.colonel.common;

import com.guflimc.colonel.common.build.CommandHandlerBuilder;
import com.guflimc.colonel.common.suggestion.Suggestion;
import com.guflimc.colonel.common.tree.CommandHandler;
import com.guflimc.colonel.common.tree.CommandTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class Colonel<S> {

    private final CommandTree tree = new CommandTree();

    //

    public void register(@NotNull String path, @NotNull CommandHandler handler) {
        tree.register(path, handler);
    }

    public void register(@NotNull String path, @NotNull Consumer<CommandHandlerBuilder<S>> buildFn) {
        CommandHandlerBuilder<S> builder = CommandHandlerBuilder.builder();
        buildFn.accept(builder);
        tree.register(path, builder.build());
    }

    //

    public void dispatch(S source, String input) {
        if (tree.apply(source, input)) {
            return;
        }

        // TODO handler not found
    }

    //

    public List<Suggestion> suggestions(S source, String input) {
        return tree.suggestions(source, input, input.length());
    }

    public List<Suggestion> suggestions(S source, String input, int cursor) {
        return tree.suggestions(source, input, cursor);
    }

}
