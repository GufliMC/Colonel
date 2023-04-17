package com.guflimc.colonel.common;

import com.guflimc.colonel.common.definition.CommandDefinition;
import com.guflimc.colonel.common.broker.Delegate;
import com.guflimc.colonel.common.broker.Handler;
import com.guflimc.colonel.common.parser.CommandInput;
import com.guflimc.colonel.common.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColonelCommandHandler extends Handler {

    public ColonelCommandHandler(@NotNull CommandDefinition definition) {
        super(definition);
    }

    @Override
    public Delegate prepare(Object source, CommandInput input) {
        return null;
    }

    @Override
    public List<Suggestion> suggestions(Object source, CommandInput input) {
        return super.suggestions(source, input);
    }

}
