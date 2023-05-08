package com.guflimc.colonel.common.dispatch.tree;

import com.guflimc.colonel.common.build.CommandContext;

public interface CommandDelegate extends Runnable {

    CommandContext context();

}
