package com.guflimc.colonel.common.command.syntax;

import com.guflimc.colonel.common.command.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CommandParameterSuggestionProvider {

    List<CommandParameterSuggestion> suggest(@NotNull CommandContext context, @NotNull String input);

}
