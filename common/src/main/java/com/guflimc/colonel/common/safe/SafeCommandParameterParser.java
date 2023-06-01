package com.guflimc.colonel.common.safe;

@FunctionalInterface
public interface SafeCommandParameterParser<S> {

    Object parse(SafeCommandContext<S> context, String input) throws Throwable;

}
