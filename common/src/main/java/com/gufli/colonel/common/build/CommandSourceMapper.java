package com.gufli.colonel.common.build;

@FunctionalInterface
public interface CommandSourceMapper {

    Object map(Object source) throws Throwable;

}