package com.guflimc.colonel.common.build;

@FunctionalInterface
public interface CommandSourceMapper {

    Object map(Object source) throws Exception;

}