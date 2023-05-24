package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.test.util.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelPlaceholderTests {

    private final Person person = new Person("John Doe");
    private final Colonel<Person> colonel = new Colonel<>();

    @Test
    public void dispatchSingleLiteral() {
        colonel.placeholder("foo", "fiz");
        colonel.builder().path("%foo%")
                .executor(ctx -> {
                    ctx.source().send("buz");
                })
                .register();

        colonel.dispatch(person, "fiz");
        assertEquals("buz", person.read());
    }

    @Test
    public void dispatchMultiLiteral() {
        colonel.placeholder("foo", "fiz");
        colonel.builder().path("%foo% baz")
                .executor(ctx -> {
                    ctx.source().send("buz");
                })
                .register();

        colonel.dispatch(person, "fiz baz");
        assertEquals("buz", person.read());
    }

    @Test
    public void dispatchMultiPlaceholder() {
        colonel.placeholder("foo", "fiz");
        colonel.placeholder("bar", "buz");
        colonel.builder().path("%foo% %bar%")
                .executor(ctx -> {
                    ctx.source().send("buz");
                })
                .register();

        colonel.dispatch(person, "fiz buz");
        assertEquals("buz", person.read());
    }

}
