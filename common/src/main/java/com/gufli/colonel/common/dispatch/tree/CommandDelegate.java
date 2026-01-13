package com.gufli.colonel.common.dispatch.tree;

import com.gufli.colonel.common.build.CommandContext;

public interface CommandDelegate extends Runnable {

    CommandContext context();

}
