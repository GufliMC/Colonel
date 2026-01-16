package com.gufli.colonel.hytale.annotations.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to configure the help info of this parameter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParameterHelp {

    String description() default "";

    String type() default "";

    String usage() default "";

    String[] examples() default "";

}
