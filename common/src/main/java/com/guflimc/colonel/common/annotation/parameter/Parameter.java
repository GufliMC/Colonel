package com.guflimc.colonel.common.annotation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Parameter {

    String NAME_INFERRED = "__NAME_INFERRED__";

    String name() default NAME_INFERRED;

    String type() default NAME_INFERRED;

}
