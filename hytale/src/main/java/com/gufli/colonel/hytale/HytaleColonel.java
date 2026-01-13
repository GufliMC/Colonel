package com.gufli.colonel.hytale;

import com.gufli.brick.i18n.common.localization.I18nLocalizer;
import com.gufli.colonel.annotation.AnnotationColonel;
import com.gufli.colonel.common.dispatch.suggestion.Suggestion;
import com.gufli.colonel.common.exception.CommandFailure;
import com.gufli.colonel.common.exception.CommandNotFoundFailure;
import com.gufli.colonel.common.exception.CommandPrepareParameterFailure;
import com.gufli.colonel.common.safe.SafeCommandContext;
import com.gufli.colonel.common.safe.SafeCommandHandlerBuilder;
import com.gufli.colonel.hytale.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class HytaleColonel<S> extends AnnotationColonel<S> {

    private I18nLocalizer localizer;
    private @Nullable BiConsumer<S, CommandFailure> errorHandler;

    public HytaleColonel(@NotNull Class<S> sourceType, @NotNull I18nLocalizer localizer) {
        super(sourceType);
        this.localizer = localizer;
    }

    @Override
    protected void build(@NotNull Method method,
                         @NotNull Map<Parameter, Function<SafeCommandContext<S>, Object>> suppliers,
                         @NotNull SafeCommandHandlerBuilder<S> builder) {
        super.build(method, suppliers, builder);

        Permission permissionConf = method.getAnnotation(Permission.class);
        if (permissionConf != null) {
            builder.condition(s -> s.hasPermission(replacePlaceholders(permissionConf.value())));
        }
    }

    @Override
    public void dispatch(S source, String input) {
        try {
            super.dispatch(source, input);
        } catch (CommandFailure failure) {
            handle(source, failure);
        }
    }

    @Override
    public List<Suggestion> suggestions(S source, String input, int cursor) {
        try {
            return super.suggestions(source, input, cursor);
        } catch (CommandFailure failure) {
            handle(source, failure);
        }
        return List.of();
    }

    //

    public void setErrorHandler(BiConsumer<S, CommandFailure> errorHandler) {
        this.errorHandler = errorHandler;
    }

    //

    private void handle(S source, CommandFailure failure) {
        if (errorHandler != null) {
            errorHandler.accept(source, failure);
            return;
        }

        // USER FACING ERRORS

        if (failure instanceof CommandNotFoundFailure) {
            sendMessage(source, "cmd.error.notfound",
                    ChatColor.RED + "Command not found: " + ChatColor.DARK_RED + "{0}", failure.command());
            return;
        }

        if (failure instanceof CommandPrepareParameterFailure pf) {
            if (pf.input() == null) {
                sendMessage(source, "cmd.error.parameter.missing",
                        ChatColor.RED + "The parameter " + ChatColor.DARK_RED + "{0}" +
                                ChatColor.RED + " is missing. Expected syntax: " + ChatColor.DARK_RED + "{1}" +
                                ChatColor.RED + ".", pf.parameter().name(), pf.path() + " " + pf.definition().toString());
                return;
            }
            if (pf.getCause() instanceof IllegalArgumentException) {
                sendMessage(source, "cmd.error.parameter",
                        ChatColor.RED + "The value " + ChatColor.DARK_RED + "{0}" +
                                ChatColor.RED + " is invalid for parameter " + ChatColor.DARK_RED + "{1}" +
                                ChatColor.RED + ".", pf.input(), pf.parameter().name());
                return;
            }
        }

        // INTERNAL ERRORS FOR THE DEVELOPER

        sendMessage(source, "cmd.error.unexpected", ChatColor.RED + "An unexpected error occured, check the console for more information.");

        if (failure.getCause() != null) {
            failure.getCause().printStackTrace();
        }
    }

    //

    void sendMessage(S source, String i18n, String fallback, Object... args) {
        if (this.localizer != null) {
            localizer.send(source, i18n, args);
            return;
        }

        String str = fallback;
        for (int i = 0; i < args.length; i++) {
            str = str.replace("{" + i + "}", args[i].toString());
        }
        source.sendMessage(str);
    }

}