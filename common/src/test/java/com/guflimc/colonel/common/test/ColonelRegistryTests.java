package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.test.util.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelRegistryTests {

    private final Person person = new Person("John Doe");
    private final Colonel<Person> colonel = new Colonel<>();

    @Test
    public void integer() {
        colonel.builder().path("foo")
                .parameter("number").type(Integer.class).done()
                .executor(ctx -> {
                    int val = ctx.argument("number");
                    ctx.source().send(val * 2);
                })
                .register();

        colonel.dispatch(person, "foo 6");
        assertEquals("12", person.read());
    }

    @Test
    public void bool() {
        colonel.builder().path("foo")
                .parameter("bool").type(Boolean.class).done()
                .executor(ctx -> {
                    boolean val = ctx.argument("bool");
                    ctx.source().send(val ? "yes" : "no");
                })
                .register();

        colonel.dispatch(person, "foo true");
        assertEquals("yes", person.read());

        colonel.dispatch(person, "foo tRuE");
        assertEquals("yes", person.read());

        colonel.dispatch(person, "foo false");
        assertEquals("no", person.read());

        colonel.dispatch(person, "foo FaLsE");
        assertEquals("no", person.read());
    }

    private enum TestEnum {
        FOO, BAR, BAZ;
    }

    @Test
    public void enumeration() {
        colonel.builder().path("foo")
                .parameter("enum").type(TestEnum.class).done()
                .executor(ctx -> {
                    TestEnum val = ctx.argument("enum");
                    ctx.source().send(val);
                })
                .register();

        colonel.dispatch(person, "foo bar");
        assertEquals("BAR", person.read());

        colonel.dispatch(person, "foo BAZ");
        assertEquals("BAZ", person.read());

        List<Suggestion> suggestions = colonel.suggestions(person, "foo ");
        assertEquals(List.of(new Suggestion("FOO"), new Suggestion("BAR"), new Suggestion("BAZ")), suggestions);

        suggestions = colonel.suggestions(person, "foo b");
        assertEquals(List.of(new Suggestion("BAR"), new Suggestion("BAZ")), suggestions);
    }

}
