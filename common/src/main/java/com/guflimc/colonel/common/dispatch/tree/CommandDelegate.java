package com.guflimc.colonel.common.dispatch.tree;

import com.guflimc.colonel.common.ext.ExtCommandContext;

public interface CommandDelegate extends Runnable {

    ExtCommandContext context();

}
