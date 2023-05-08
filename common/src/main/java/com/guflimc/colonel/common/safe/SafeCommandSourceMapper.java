package com.guflimc.colonel.common.safe;

@FunctionalInterface
public interface SafeCommandSourceMapper<S> {

    Object map(S source);

}
