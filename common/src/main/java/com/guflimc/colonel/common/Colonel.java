package com.guflimc.colonel.common;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.broker.Messenger;

public class Colonel<S> {

    private final Messenger messenger = new Messenger();

    //

    public void register(String path, CommandDefinition definition, ) {

    }


    //

    public void dispatch(S source, String input) {
        if ( messenger.apply(source, input) ) {
            return;
        }

        // TODO handler not found
    }

    //


}
