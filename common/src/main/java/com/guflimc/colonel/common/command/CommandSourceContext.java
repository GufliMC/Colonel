package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.command.handler.CommandHandler;

public interface CommandSourceContext {

    Command command();

    CommandHandler handler();

    <T> T source();

}
