package com.guflimc.colonel.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BiPredicate;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionsLogic {

    LogicalGate value() default LogicalGate.AND;

    enum LogicalGate {
        /**
         * True if all permissions test true.
         */
        AND(Integer::equals),
        /**
         * True if one permission tests false.
         */
        NAND((match, total) -> !match.equals(total)),
        /**
         * True if at least one permission tests true.
         */
        OR((match, total) -> match > 0),
        /**
         * True if exactly one permission tests true.
         */
        XOR((match, total) -> match == 1),
        /**
         * True if no permissions test true.
         */
        NOR((match, total) -> match == 0),
        /**
         * True if all or none of the permissions test true.
         */
        XNOR((match, total) -> match == 0 || match.equals(total));

        private final BiPredicate<Integer, Integer> test;

        LogicalGate(BiPredicate<Integer, Integer> test) {
            this.test = test;
        }

        public boolean test(int match, int total) {
            return test.test(match, total);
        }
    }
}
