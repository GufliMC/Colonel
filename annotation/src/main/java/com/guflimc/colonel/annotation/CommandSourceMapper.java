package com.guflimc.colonel.annotation;

@FunctionalInterface
public interface CommandSourceMapper<S> {

    Object map(S source);

}