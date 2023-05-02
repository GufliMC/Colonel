package com.guflimc.colonel.common.ext;

@FunctionalInterface
public interface ExtCommandParameterParser {

    Argument parse(ExtCommandContext context, String input);

}
