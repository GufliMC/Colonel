package com.guflimc.colonel.common.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Permission.Permissions.class)
public @interface Permission {
    String value();

    boolean invert() default false;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Permissions {
        Permission[] value();
    }
}