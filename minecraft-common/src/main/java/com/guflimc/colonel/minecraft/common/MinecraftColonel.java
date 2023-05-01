package com.guflimc.colonel.minecraft.common;

import com.guflimc.colonel.annotation.AnnotationColonel;
import net.kyori.adventure.audience.Audience;

public abstract class MinecraftColonel<S> extends AnnotationColonel<S> {

    public MinecraftColonel(Class<S> sourceType) {
        super(sourceType);

        // audience
        registry.registerSourceMapper(Audience.class, this::audience);
    }

    protected abstract Audience audience(S source);

}
