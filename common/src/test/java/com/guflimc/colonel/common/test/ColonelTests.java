package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.annotation.Command;
import com.guflimc.colonel.common.annotation.CommandPermissions;
import com.guflimc.colonel.common.annotation.CommandSource;
import com.guflimc.colonel.common.test.util.MockSource;
import com.guflimc.colonel.common.test.util.MultiOutputStream;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class ColonelTests {

    private Colonel<MockSource> colonel = new Colonel<>();

    @Test
    public void dispatch() throws CommandSyntaxException {
        colonel.registerCommands(this);

        // create mock source
        MockSource source = new MockSource("John");

        // set output stream
        PrintStream standard = System.out;
        ByteArrayOutputStream captor = new ByteArrayOutputStream();
        MultiOutputStream mos = new MultiOutputStream(standard, captor);
        System.setOut(new PrintStream(mos));

        // root command
        colonel.dispatcher().execute("setage 27", source);
        assertEquals(27, source.age());

        colonel.dispatcher().execute("getage", source);
        assertEquals("Your age is 27.", captor.toString().trim());
        captor.reset();

        // sub command
        colonel.dispatcher().execute("set age 31", source);
        assertEquals(31, source.age());

        colonel.dispatcher().execute("getage", source);
        assertEquals("Your age is 31.", captor.toString().trim());
    }

    @Test
    public void requirePermissions() {
        colonel.setPermissionValidator(MockSource::hasPermission);
        colonel.registerCommands(this);

        // create mock source
        MockSource source = new MockSource("Jack");

        // for "get age", source must have "command.getage" or not have "command.muggle"
        assertDoesNotThrow(() -> colonel.dispatcher().execute("get age", source));

        source.addPermission("command.muggle");
        assertThrows(CommandSyntaxException.class, () -> colonel.dispatcher().execute("get age", source));

        // for "set age", source must have exactly one of "command.setage" or "command.muggle"
        assertDoesNotThrow(() -> colonel.dispatcher().execute("set age 32", source));

        source.addPermission("command.setage");
        assertThrows(CommandSyntaxException.class, () -> colonel.dispatcher().execute("set age 47", source));
    }

    // command methods

    @Command("getage")
    @Command("get age")
    @CommandPermissions(value = {
            @CommandPermissions.CommandPermission("command.getage"),
            @CommandPermissions.CommandPermission(value = "command.muggle", negate = true),
    }, gate = CommandPermissions.LogicalGate.OR)
    public void getAge(@CommandSource MockSource source) {
        System.out.printf("Your age is %d.%n", source.age());
    }

    @Command("setage")
    @Command("set age")
    @CommandPermissions(value = {
            @CommandPermissions.CommandPermission("command.setage"),
            @CommandPermissions.CommandPermission("command.muggle"),
    }, gate = CommandPermissions.LogicalGate.XOR)
    public void setAge(@CommandSource MockSource source, int age) {
        source.setAge(age);
    }

}
