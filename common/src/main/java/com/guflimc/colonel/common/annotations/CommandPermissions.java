package com.guflimc.colonel.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandPermissions {

    CommandPermission[] value();

    LogicalGate gate() default LogicalGate.AND;

    enum LogicalGate {
        AND, NAND, OR, XOR, NOR, XNOR;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface CommandPermission {
        String value();

        boolean negate() default false;
    }
}
