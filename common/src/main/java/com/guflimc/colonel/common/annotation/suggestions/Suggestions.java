package com.guflimc.colonel.common.annotation.suggestions;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Suggestions {

    @NotNull String name();

    @NotNull Class<?> target();
    
}
