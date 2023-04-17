package com.guflimc.colonel.annotation.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Command.Commands.class)
public @interface Command {

    @NotNull String value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Commands {
        Command[] value();
    }
}
