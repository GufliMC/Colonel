package com.guflimc.colonel.common.command;

import com.guflimc.colonel.common.ColonelConfig;
import com.guflimc.colonel.common.annotation.command.Command;
import com.guflimc.colonel.common.annotation.command.Permission;
import com.guflimc.colonel.common.annotation.command.PermissionsLogic;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;

import java.lang.reflect.Method;

public class AnnotatedCommand<S> {

    private final Method method;

    private final Command command;
    private final PermissionsLogic.LogicalGate permissionsLogic;
    private final Permission[] permissions;

    private final AnnotatedParameter[] parameters;

    public AnnotatedCommand(Method method) {
        this.method = method;

        // command literals
        command = method.getAnnotation(Command.class);

        // permissions logic
        PermissionsLogic permissionsLogic = method.getAnnotation(PermissionsLogic.class);
        this.permissionsLogic = permissionsLogic == null ? PermissionsLogic.LogicalGate.AND : permissionsLogic.value();

        // permissions
        permissions = method.getAnnotationsByType(Permission.class);

        // parameters
        parameters = new AnnotatedParameter[0];
    }

    public Method method() {
        return method;
    }

    public PermissionsLogic.LogicalGate permissionsLogic() {
        return permissionsLogic;
    }

    public Permission[] permissions() {
        return permissions;
    }

    public void register(CommandNode<S> root) {
        // TODO
        return null;
    }

    public int invoke(ColonelConfig<S> config, CommandContext<S> context) {

    }

}
