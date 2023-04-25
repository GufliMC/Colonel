package com.guflimc.colonel.annotation.annotations.parameter;

import com.guflimc.colonel.common.definition.CommandParameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Parameter {

    /**
     * The name of the parameter. Will try to use the name of the compiled parameter by default.
     */
    String value() default "";

    /**
     * How the argument value should be read from the input.
     */
    CommandParameter.ReadMode read() default CommandParameter.ReadMode.STRING;

}
