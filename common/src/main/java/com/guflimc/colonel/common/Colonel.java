package com.guflimc.colonel.common;

import com.guflimc.colonel.common.tree.CommandHandler;
import com.guflimc.colonel.common.tree.CommandTree;
import org.jetbrains.annotations.NotNull;

public class Colonel<S> {

    private final CommandTree tree = new CommandTree();

    //

    public void register(@NotNull String path, @NotNull CommandHandler handler) {
        tree.register(path, handler);
    }

    //

    public void dispatch(S source, String input) {
        if (tree.apply(source, input)) {
            return;
        }

        // TODO handler not found
    }

    //

}
