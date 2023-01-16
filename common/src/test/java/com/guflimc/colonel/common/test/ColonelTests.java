package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.annotation.*;
import com.guflimc.colonel.common.test.util.Person;
import com.guflimc.colonel.common.test.util.MultiOutputStream;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ColonelTests {

    private final Colonel<Person> colonel = new Colonel<>();

    @BeforeEach
    public void setup() {
        colonel.config().withPermmissionTester(Person::hasPermission);
        colonel.config().withArgumentTypeParser(Person.Gender.class, Person.Gender::valueOf);
    }

    @Test
    public void dispatch() throws CommandSyntaxException {
        colonel.registerCommands(this);

        // create mock source
        Person source = new Person("John");
        source.addPermission("command.setage");
        source.addPermission("command.setage");

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
        colonel.registerCommands(this);

        // create mock source
        Person source = new Person("Jack");

        // for "get age", source must have "command.getage" or not have "command.muggle"
        assertDoesNotThrow(() -> colonel.dispatcher().execute("get age", source));

        source.addPermission("command.muggle");
        assertThrows(CommandSyntaxException.class, () -> colonel.dispatcher().execute("get age", source));

        // for "set age", source must have "command.setage"
        assertThrows(CommandSyntaxException.class, () -> colonel.dispatcher().execute("set age 47", source));

        source.addPermission("command.setage");
        assertDoesNotThrow(() -> colonel.dispatcher().execute("set age 32", source));
    }

    @Test
    public void argumentMappingAndSuggestions() throws CommandSyntaxException {
        colonel.registerCommands(this);

        Person source = new Person("Jerry");

        // test argument mapping
        colonel.dispatcher().execute("setgender MAN", source);
        assertEquals(Person.Gender.MAN, source.gender());

        // test argument suggestions
        ParseResults<Person> parse = colonel.dispatcher().parse("setgender ", source);
        List<String> suggestions = colonel.dispatcher().getCompletionSuggestions(parse).join().getList().stream()
                .map(Suggestion::getText).toList();
        assertTrue(suggestions.contains("MAN"));
        assertTrue(suggestions.contains("WOMAN"));

        // test argument suggestions
        parse = colonel.dispatcher().parse("set age ", source);
        suggestions = colonel.dispatcher().getCompletionSuggestions(parse).join().getList().stream()
                .map(Suggestion::getText).toList();
        assertFalse(suggestions.contains(source.age() + ""));
        assertTrue(suggestions.contains("49"));
    }

    // command methods

    @SuggestionProvider(value = "age", target = int.class)
    public List<Integer> ageSuggestions(CommandContext<Person> ctx) {
        return IntStream.range(0, 100).filter(i -> i != ctx.getSource().age()).boxed().toList();
    }

    @Command("getage")
    @Command("get age")
    @PermissionsLogic(PermissionsLogic.LogicalGate.OR)
    @Permission("command.getage")
    @Permission(value = "command.muggle", invert = true)
    public void getAge(@CommandSource Person source) {
        System.out.printf("Your age is %d.%n", source.age());
    }

    @Command("setage")
    @Command("set age")
    @Permission("command.setage")
    public void setAge(@CommandSource Person source, @Suggestions("age") int age) {
        source.setAge(age);
    }

    @Command("setgender")
    public void setGender(@CommandSource Person source, Person.Gender gender) {
        source.setGender(gender);
    }

}
