package com.guflimc.colonel.annotation.annotations.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Suggestions {

    /**
     * The array of fixed suggestions to use for this parameter.
     */
    String[] value();

}
