package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.dispatch.definition.CommandDefinition;
import com.guflimc.colonel.common.dispatch.definition.CommandParameter;
import com.guflimc.colonel.common.dispatch.definition.ReadMode;
import com.guflimc.colonel.common.dispatch.parser.CommandInput;
import com.guflimc.colonel.common.dispatch.parser.CommandInputReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandInputReaderTests {

    @Test
    public void stringDoubleQuoted() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1 });

        CommandInputReader reader = new CommandInputReader(def, "\"foo bar\"");
        CommandInput input = reader.read();

        assertEquals("foo bar", input.argument(p1));
        assertEquals(p1, input.cursor());
        assertTrue(input.errors().isEmpty());
        assertNull(input.excess());
    }

    @Test
    public void stringSingleQuoted() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1 });

        CommandInputReader reader = new CommandInputReader(def, "'foo bar'");
        CommandInput input = reader.read();

        assertEquals("foo bar", input.argument(p1));
        assertEquals(p1, input.cursor());
        assertTrue(input.errors().isEmpty());
        assertNull(input.excess());
    }

    @Test
    public void stringUnquoted() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1 });

        CommandInputReader reader = new CommandInputReader(def, "foo");
        CommandInput input = reader.read();

        assertEquals("foo", input.argument(p1));
        assertEquals(p1, input.cursor());
        assertTrue(input.errors().isEmpty());
        assertNull(input.excess());
    }

    @Test
    public void greedy() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.GREEDY);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1 });

        CommandInputReader reader = new CommandInputReader(def, "foo bar");
        CommandInput input = reader.read();

        assertEquals("foo bar", input.argument(p1));
        assertEquals(p1, input.cursor());
        assertTrue(input.errors().isEmpty());
        assertNull(input.excess());
    }

    //

    @Test
    public void empty() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1 });

        CommandInputReader reader = new CommandInputReader(def, "");
        CommandInput input = reader.read();

        assertEquals("", input.argument(p1));
        assertEquals(p1, input.cursor());
        assertTrue(input.errors().isEmpty());
        assertNull(input.excess());
    }

    @Test
    public void space() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1 });

        CommandInputReader reader = new CommandInputReader(def, " ");
        CommandInput input = reader.read();

        assertEquals("", input.argument(p1));
        assertEquals(p1, input.cursor());
        assertTrue(input.errors().isEmpty());
        assertNull(input.excess());
    }

    //

    @Test
    public void stringQuotedExcess() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1 });

        CommandInputReader reader = new CommandInputReader(def, "\"foo bar\" baz");
        CommandInput input = reader.read();

        assertEquals("foo bar", input.argument(p1));
        assertEquals(input.excess(), "baz");
        assertTrue(input.errors().isEmpty());
    }

    @Test
    public void stringUnquotedExcess() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1 });

        CommandInputReader reader = new CommandInputReader(def, "foo bar");
        CommandInput input = reader.read();

        assertEquals("foo", input.argument(p1));
        assertEquals(input.excess(), "bar");
        assertTrue(input.errors().isEmpty());
    }

    //

    @Test
    public void stringXgreedy() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandParameter p2 = new CommandParameter("p2", ReadMode.GREEDY);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1, p2 });

        CommandInputReader reader = new CommandInputReader(def, "\"foo bar\" baz fizz buzz");
        CommandInput input = reader.read();

        assertEquals("foo bar", input.argument(p1));
        assertEquals("baz fizz buzz", input.argument(p2));
        assertNull(input.excess());
        assertTrue(input.errors().isEmpty());
    }

    @Test
    public void greedyXstring() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.GREEDY);
        CommandParameter p2 = new CommandParameter("p2", ReadMode.STRING);
        assertThrows(IllegalArgumentException.class, () -> new CommandDefinition(new CommandParameter[]{ p1, p2 }));
    }

    //

    @Test
    public void cursorFront() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandParameter p2 = new CommandParameter("p2", ReadMode.STRING);
        CommandParameter p3 = new CommandParameter("p3", ReadMode.STRING);
        CommandParameter p4 = new CommandParameter("p4", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1, p2, p3, p4 });

        CommandInputReader reader = new CommandInputReader(def, "foo bar baz fizz", 8);
        CommandInput input = reader.read();

        assertEquals(p3, input.cursor());
    }

    @Test
    public void cursorMiddle() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandParameter p2 = new CommandParameter("p2", ReadMode.STRING);
        CommandParameter p3 = new CommandParameter("p3", ReadMode.STRING);
        CommandParameter p4 = new CommandParameter("p4", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1, p2, p3, p4 });

        CommandInputReader reader = new CommandInputReader(def, "foo bar baz fizz", 5);
        CommandInput input = reader.read();

        assertEquals(p2, input.cursor());
    }

    @Test
    public void cursorEnd() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandParameter p2 = new CommandParameter("p2", ReadMode.STRING);
        CommandParameter p3 = new CommandParameter("p3", ReadMode.STRING);
        CommandParameter p4 = new CommandParameter("p4", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1, p2, p3, p4 });

        CommandInputReader reader = new CommandInputReader(def, "foo bar baz fizz", 3);
        CommandInput input = reader.read();

        assertEquals(p1, input.cursor());
    }

    @Test
    public void cursorEmptyInput() {
        CommandParameter p1 = new CommandParameter("p1", ReadMode.STRING);
        CommandParameter p2 = new CommandParameter("p2", ReadMode.STRING);
        CommandParameter p3 = new CommandParameter("p3", ReadMode.STRING);
        CommandParameter p4 = new CommandParameter("p4", ReadMode.STRING);
        CommandDefinition def = new CommandDefinition(new CommandParameter[]{ p1, p2, p3, p4 });

        CommandInputReader reader = new CommandInputReader(def, "", 0);
        CommandInput input = reader.read();

        assertEquals(p1, input.cursor());
    }

}
