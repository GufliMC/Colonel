package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.syntax.CommandSyntax;

public interface CommandSourceContext {

    Command command();

    CommandSyntax syntax();

    <T> T source();

}
