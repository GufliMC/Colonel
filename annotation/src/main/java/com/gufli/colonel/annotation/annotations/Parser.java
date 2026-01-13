package com.gufli.colonel.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface Parser {

    /**
     * The name of the parser
     */
    String value() default "";


    /**
     * The type this parser applies to.
     */
    Class<?> type() default Void.class;

}
