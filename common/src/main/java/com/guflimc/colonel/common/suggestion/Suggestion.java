package com.guflimc.colonel.common.suggestion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Suggestion(@NotNull String value, @Nullable String description) {

    public Suggestion(@NotNull String value) {
        this(value, null);
    }

}
