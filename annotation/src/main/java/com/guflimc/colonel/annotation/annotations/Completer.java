package com.guflimc.colonel.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Completer {

    /**
     * The name of the completer.
     */
    String value() default "";

    /**
     * The type this completer applies to.
     */
    Class<?> type() default Void.class;
}
