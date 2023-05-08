package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.test.util.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelDispatchTests {

    private final Person person = new Person("John Doe");
    private final Colonel<Person> colonel = new Colonel<>();

    @Test
    public void dispatchSingleLiteral() {
        colonel.builder().path("foo")
                .executor(ctx -> {
                    ctx.source().send("bar");
                })
                .register();

        colonel.dispatch(person, "foo");
        assertEquals("bar", person.read());
    }

    @Test
    public void dispatchMultiLiteral() {
        colonel.builder().path("foo bar")
                .executor(ctx -> {
                    ctx.source().send("baz");
                })
                .register();

        colonel.dispatch(person, "foo bar");
        assertEquals("baz", person.read());
    }

    @Test
    public void dispatchSingleArgument() {
        colonel.builder().path("foo")
                .string("p1", s -> s).done()
                .executor(ctx -> {
                    person.send(ctx.argument("p1"));
                })
                .register();

        colonel.dispatch(person, "foo bar");
        assertEquals("bar", person.read());
    }

    @Test
    public void dispatchMultiLiteralMultiArgument() {
        colonel.builder().path("foo bar")
                .string("p1", s -> s).done()
                .string("p2", s -> s).done()
                .executor(ctx -> {
                    person.send(ctx.argument("p1"));
                    person.send(ctx.argument("p2"));
                })
                .register();

        colonel.dispatch(person, "foo bar baz fizz");
        assertEquals("baz", person.read());
        assertEquals("fizz", person.read());
    }

    @Test
    public void dispatchSamePathDifferentArgumentLength() {
        colonel.builder().path("foo bar")
                .string("p1", s -> s).done()
                .string("p2", s -> s).done()
                .executor(ctx -> {
                    person.send("a");
                }).register();

        colonel.builder().path("foo bar")
                .string("p1", s -> s).done()
                .string("p2", s -> s).done()
                .string("p3", s -> s).done()
                .executor(ctx -> {
                    person.send("b");
                }).register();

        colonel.dispatch(person, "foo bar baz fizz");
        assertEquals("a", person.read());

        colonel.dispatch(person, "foo bar baz fizz buzz");
        assertEquals("b", person.read());
    }

    @Test
    public void dispatchSamePathSameArgumentLength() {
        colonel.builder().path("foo bar")
                .string("p1", s -> s).done()
                .string("p2", (ctx, s) -> { throw new RuntimeException(); }).done()
                .executor(ctx -> {
                    person.send("a");
                }).register();

        colonel.builder().path("foo bar")
                .string("p1", s -> s).done()
                .string("p2", s -> s).done()
                .executor(ctx -> {
                    person.send("b");
                }).register();

        colonel.dispatch(person, "foo bar baz fizz");
        assertEquals("b", person.read());
    }

}
