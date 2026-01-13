package com.gufli.colonel.annotation.annotations.parameter;

import com.gufli.colonel.annotation.annotations.Command;
import com.gufli.colonel.common.dispatch.definition.ReadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used on the parameters of commands, parsers and completers.
 * Only {@link #value()} will have affect on parsers and completers, other values are ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Parameter {

    /**
     * The name of the parameter. Will try to use the name of the compiled parameter by default.
     */
    String value() default "";

    /**
     * How the argument value should be read from the input.
     * This only affects parameters of a {@link Command} method.
     */
    ReadMode read() default ReadMode.STRING;

    /**
     * The name of the completer to use for this parameter.
     * This only affects parameters of a {@link Command} method.
     */
    String completer() default "";

    /**
     * The name of the parser to use for this parameter.
     * This only affects parameters of a {@link Command} method.
     */
    String parser() default "";

}
