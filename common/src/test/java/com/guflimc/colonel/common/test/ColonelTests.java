package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.annotation.Command;
import com.guflimc.colonel.common.annotation.CommandSource;
import com.guflimc.colonel.common.test.util.MultiOutputStream;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelTests {

    private class MockSource {
        int age = 17;
    }

    private final Colonel<MockSource> colonel = new Colonel<>();

    @Test
    public void test() throws CommandSyntaxException {
        colonel.registerCommands(this);

        // create mock source
        MockSource source = new MockSource();

        // set output stream
        PrintStream standard = System.out;
        ByteArrayOutputStream captor = new ByteArrayOutputStream();
        MultiOutputStream mos = new MultiOutputStream(standard, captor);
        System.setOut(new PrintStream(mos));


        // root command
        colonel.dispatcher().execute("setage 27", source);
        assertEquals(27, source.age);

        colonel.dispatcher().execute("getage", source);
        assertEquals("Your age is 27.", captor.toString().trim());
        captor.reset();

        // sub command
        colonel.dispatcher().execute("set age 31", source);
        assertEquals(31, source.age);

        colonel.dispatcher().execute("getage", source);
        assertEquals("Your age is 31.", captor.toString().trim());
    }

    // command methods

    @Command("getage")
    @Command("get age")
    public void getAge(@CommandSource MockSource source) {
        System.out.printf("Your age is %d.%n", source.age);
    }

    @Command("setage")
    @Command("set age")
    public void setAge(@CommandSource MockSource source, int age) {
        source.age = age;
    }

}
