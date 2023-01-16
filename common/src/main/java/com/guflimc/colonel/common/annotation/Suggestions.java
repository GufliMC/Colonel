package com.guflimc.colonel.common.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Suggestions {

    String TYPE_OR_NAME_INFERRED = "__TYPE_OR_NAME_INFERRED__";

    @NotNull String value() default TYPE_OR_NAME_INFERRED;
}
