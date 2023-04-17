package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.command.CommandDispatcher;
import com.guflimc.colonel.common.command.builder.CommandSyntaxBuilder;
import com.guflimc.colonel.common.registry.TypeRegistryContainer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class CommandDispatcherTests {

    private final CommandDispatcher dispatcher = new CommandDispatcher();
    private final TypeRegistryContainer registry = new TypeRegistryContainer();

    @Test
    public void dispatchSingleLiteral() {
        AtomicReference<String> ref = new AtomicReference<>();

        dispatcher.register(CommandSyntaxBuilder.with(registry).withLiterals("ping").build(),
                (context) -> ref.set("pong"));

        assertNull(ref.get());

        dispatcher.dispatch(null, "ping");
        assertEquals("pong", ref.get());
    }

    @Test
    public void dispatchMultiLiteral() {
        AtomicReference<String> ref = new AtomicReference<>();

        dispatcher.register(CommandSyntaxBuilder.with(registry).withLiterals("foo bar").build(),
                (context) -> ref.set("baz"));

        assertNull(ref.get());

        dispatcher.dispatch(null, "foo bar");
        assertEquals("baz", ref.get());
    }

    @Test
    public void dispatchSingleArgument() {
        AtomicReference<String> ref = new AtomicReference<>();

        dispatcher.register(CommandSyntaxBuilder.with(registry).withLiterals("foo")
                        .withParameter("bar", String.class).build(),
                (context) -> ref.set(context.argument("bar")));

        assertNull(ref.get());

        dispatcher.dispatch(null, "foo fiz");
        assertEquals("fiz", ref.get());
    }

    @Test
    public void dispatchMultiLiteralMultiArgument() {
        AtomicReference<Integer> baz = new AtomicReference<>();
        AtomicReference<Boolean> fiz = new AtomicReference<>();

        dispatcher.register(
                CommandSyntaxBuilder.with(registry).withLiterals("foo bar")
                        .withParameter("baz", Integer.class)
                        .withParameter("fiz", Boolean.class)
                        .build(),
                (context) -> {
                    baz.set(context.argument("baz"));
                    fiz.set(context.argument("fiz"));
                }
        );

        assertNull(baz.get());
        assertNull(fiz.get());

        dispatcher.dispatch(null, "foo bar 42 true");
        assertEquals(42, baz.get());
        assertTrue(fiz.get());
    }

    @Test
    public void dispatchWithSource() {
        AtomicReference<String> ref = new AtomicReference<>();

        dispatcher.register(CommandSyntaxBuilder.with(registry).withLiterals("foo bar").build(),
                (context) -> ref.set(context.source())
        );

        assertNull(ref.get());

        dispatcher.dispatch("eric", "foo bar");
        assertEquals("eric", ref.get());
    }

}
