package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.syntax.CommandSyntax;

public interface CommandSourceContext {

    Command command();

    CommandSyntax syntax();

    @SuppressWarnings("unchecked")
    default <T> T source() {
        return (T) command().source();
    }

}
