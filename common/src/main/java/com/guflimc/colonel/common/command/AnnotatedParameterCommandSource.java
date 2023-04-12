package com.guflimc.colonel.common.command;

import com.mojang.brigadier.context.CommandContext;

import java.lang.reflect.Parameter;

public class AnnotatedParameterCommandSource extends AnnotatedParameter {

    public AnnotatedParameterCommandSource(Parameter parameter) {
        super(parameter);
    }

    public <S, R> R parse(CommandContext<S> context) {
        return (R) context.getSource();
    }

}
