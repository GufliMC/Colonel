package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.build.Argument;
import com.guflimc.colonel.common.test.util.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelDispatchTests {

    private final Person person = new Person("John Doe");
    private final Colonel<Person> colonel = new Colonel<>();

    @Test
    public void dispatchSingleLiteral() {
        colonel.register("foo", b -> b.executor(ctx -> {
            ctx.source().send("bar");
        }));

        colonel.dispatch(person, "foo");
        assertEquals("bar", person.read());
    }

    @Test
    public void dispatchMultiLiteral() {
        colonel.register("foo bar", b -> b.executor(ctx -> {
            person.send("baz");
        }));

        colonel.dispatch(person, "foo bar");
        assertEquals("baz", person.read());
    }

    @Test
    public void dispatchSingleArgument() {
        colonel.register("foo", b -> b
                .word("p1", (ctx, s) -> Argument.success(s))
                .executor(ctx -> {
            person.send(ctx.argument("p1"));
        }));

        colonel.dispatch(person, "foo bar");
        assertEquals("bar", person.read());
    }

    @Test
    public void dispatchMultiLiteralMultiArgument() {
        colonel.register("foo bar", b -> b
                .word("p1", (ctx, s) -> Argument.success(s))
                .word("p2", (ctx, s) -> Argument.success(s))
                .executor(ctx -> {
                    person.send(ctx.argument("p1"));
                    person.send(ctx.argument("p2"));
                }));

        colonel.dispatch(person, "foo bar baz fizz");
        assertEquals("baz", person.read());
        assertEquals("fizz", person.read());
    }

    @Test
    public void dispatchSamePathDifferentArgumentLength() {
        colonel.register("foo bar", b -> b
                .word("p1", (ctx, s) -> Argument.success(s))
                .word("p2", (ctx, s) -> Argument.success(s))
                .executor(ctx -> {
                    person.send("a");
                }));

        colonel.register("foo bar", b -> b
                .word("p1", (ctx, s) -> Argument.success(s))
                .word("p2", (ctx, s) -> Argument.success(s))
                .word("p3", (ctx, s) -> Argument.success(s))
                .executor(ctx -> {
                    person.send("b");
                }));

        colonel.dispatch(person, "foo bar baz fizz");
        assertEquals("a", person.read());

        colonel.dispatch(person, "foo bar baz fizz buzz");
        assertEquals("b", person.read());
    }

    @Test
    public void dispatchSamePathSameArgumentLength() {
        colonel.register("foo bar", b -> b
                .word("p1", (ctx, s) -> Argument.success(s))
                .word("p2", (ctx, s) -> Argument.fail(() -> {}))
                .executor(ctx -> {
                    person.send("a");
                }));

        colonel.register("foo bar", b -> b
                .word("p1", (ctx, s) -> Argument.success(s))
                .word("p2", (ctx, s) -> Argument.success(s))
                .executor(ctx -> {
                    person.send("b");
                }));

        colonel.dispatch(person, "foo bar baz fizz");
        assertEquals("b", person.read());
    }

}
