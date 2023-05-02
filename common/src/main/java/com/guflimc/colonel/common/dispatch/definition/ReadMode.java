package com.guflimc.colonel.common.dispatch.definition;

public enum ReadMode {
    /**
     * Read a single word or if the first character is a quote, read until the next quote.
     **/
    STRING,
    /**
     * Read the remaining input.
     **/
    GREEDY;
}