package com.guflimc.colonel.common.ext;

@FunctionalInterface
public interface ExtCommandExecutor {

    void execute(ExtCommandContext context);

}
