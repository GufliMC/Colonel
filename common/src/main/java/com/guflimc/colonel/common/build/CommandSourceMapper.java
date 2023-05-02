package com.guflimc.colonel.common.build;

@FunctionalInterface
public interface CommandSourceMapper<S> {

    Object map(S source);

}
