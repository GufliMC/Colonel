package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.command.CommandDispatcher;
import com.guflimc.colonel.common.command.CommandSourceContext;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class CommandDispatcherTests {

    private final CommandDispatcher dispatcher = new CommandDispatcher();

    @Test
    public void dispatchSingleLiteral() {
        AtomicReference<String> ref = new AtomicReference<>();

        dispatcher.register(
                cs -> cs.withLiterals("ping"),
                (context) -> ref.set("pong")
        );

        assertNull(ref.get());

        dispatcher.dispatch(null, "ping");
        assertEquals("pong", ref.get());
    }

    @Test
    public void dispatchMultiLiteral() {
        AtomicReference<String> ref = new AtomicReference<>();

        dispatcher.register(
                cs -> cs.withLiterals("foo bar"),
                (context) -> ref.set("baz")
        );

        assertNull(ref.get());

        dispatcher.dispatch(null, "foo bar");
        assertEquals("baz", ref.get());
    }

    @Test
    public void dispatchSingleArgument() {
        AtomicReference<String> ref = new AtomicReference<>();

        dispatcher.context().registerParameterType("string", String.class, (context, input) -> input);
        dispatcher.register(
                cs -> cs.withLiterals("foo").withParameter("bar", String.class),
                (context) -> ref.set(context.get("bar"))
        );

        assertNull(ref.get());

        dispatcher.dispatch(null, "foo fiz");
        assertEquals("fiz", ref.get());
    }

    @Test
    public void dispatchMultiLiteralMultiArgument() {
        AtomicReference<Integer> baz = new AtomicReference<>();
        AtomicReference<Boolean> fiz = new AtomicReference<>();

        dispatcher.context().registerParameterType("integer", Integer.class, (context, input) -> Integer.parseInt(input));
        dispatcher.context().registerParameterType("boolean", Boolean.class, (context, input) -> Boolean.parseBoolean(input));
        dispatcher.register(
                cs -> cs.withLiterals("foo bar")
                        .withParameter("baz", Integer.class)
                        .withParameter("fiz", Boolean.class),
                (context) -> {
                    baz.set(context.get("baz"));
                    fiz.set(context.get("fiz"));
                });

        assertNull(baz.get());
        assertNull(fiz.get());

        dispatcher.dispatch(null, "foo bar 42 true");
        assertEquals(42, baz.get());
        assertTrue(fiz.get());
    }

    @Test
    public void dispatchWithSourceByTypeConversion() {
        AtomicReference<String> ref = new AtomicReference<>();

        dispatcher.context().registerSourceParser("string", String.class,
                CommandSourceContext::source);

        dispatcher.register(
                cs -> cs.withLiterals("foo bar"),
                (context) -> ref.set(context.source(String.class))
        );

        assertNull(ref.get());

        dispatcher.dispatch("eric", "foo bar");
        assertEquals("eric", ref.get());
    }

    @Test
    public void dispatchWithSourceByNameConversion() {
        AtomicReference<Integer> ref = new AtomicReference<>();

        dispatcher.context().registerSourceParser("number", Integer.class,
                (context) -> Integer.parseInt(context.source()));

        dispatcher.register(
                cs -> cs.withLiterals("foo bar"),
                (context) -> ref.set(context.source("number"))
        );

        assertNull(ref.get());

        dispatcher.dispatch("42", "foo bar");
        assertEquals(42, ref.get());
    }


//    @Test
//    public void requirePermissions() {
//        colonel.registerCommands(this);
//
//        // create mock source
//        Person source = new Person("Jack");
//
//        // for "get age", source must have "command.getage" or not have "command.muggle"
//        assertDoesNotThrow(() -> colonel.dispatcher().execute("get age", source));
//
//        source.addPermission("command.muggle");
//        assertThrows(CommandSyntaxException.class, () -> colonel.dispatcher().execute("get age", source));
//
//        // for "set age", source must have "command.setage"
//        assertThrows(CommandSyntaxException.class, () -> colonel.dispatcher().execute("set age 47", source));
//
//        source.addPermission("command.setage");
//        assertDoesNotThrow(() -> colonel.dispatcher().execute("set age 32", source));
//    }
//
//    @Test
//    public void argumentMappingAndSuggestions() throws CommandSyntaxException {
//        colonel.registerCommands(this);
//
//        Person source = new Person("Jerry");
//
//        // test argument mapping
//        colonel.dispatcher().execute("setgender MAN", source);
//        assertEquals(Person.Gender.MAN, source.gender());
//
//        // test argument suggestions
//        ParseResults<Person> parse = colonel.dispatcher().parse("setgender ", source);
//        List<String> suggestions = colonel.dispatcher().getCompletionSuggestions(parse).join().getList().stream()
//                .map(Suggestion::getText).toList();
//        assertTrue(suggestions.contains("MAN"));
//        assertTrue(suggestions.contains("WOMAN"));
//
//        // test argument suggestions
//        parse = colonel.dispatcher().parse("set age ", source);
//        suggestions = colonel.dispatcher().getCompletionSuggestions(parse).join().getList().stream()
//                .map(Suggestion::getText).toList();
//        assertFalse(suggestions.contains(source.age() + ""));
//        assertTrue(suggestions.contains("49"));
//    }
//
//    // command methods
//
//    @Suggestions(value = "age", target = int.class)
//    public List<Integer> ageSuggestions(CommandContext<Person> ctx) {
//        return IntStream.range(0, 100).filter(i -> i != ctx.getSource().age()).boxed().toList();
//    }
//
//    @Command("getage")
//    @Command("get age")
//    @PermissionsLogic(PermissionsLogic.LogicalGate.OR)
//    @Permission("command.getage")
//    @Permission(value = "command.muggle", invert = true)
//    public void getAge(@CommandSource Person source) {
//        System.out.printf("Your age is %d.%n", source.age());
//    }
//
//    @Command("setage")
//    @Command("set age")
//    @Permission("command.setage")
//    public void setAge(@CommandSource Person source, @WithSuggestions("age") int age) {
//        source.setAge(age);
//    }
//
//    @Command("setgender")
//    public void setGender(@CommandSource Person source, Person.Gender gender) {
//        source.setGender(gender);
//    }

}
