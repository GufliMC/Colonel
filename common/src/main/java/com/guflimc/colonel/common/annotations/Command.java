package com.guflimc.colonel.common.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Command.Commands.class)
public @interface Command {
    String value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Commands {
        Command[] value();
    }
}
