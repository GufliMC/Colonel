package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.dispatch.suggestion.Suggestion;
import com.guflimc.colonel.common.test.util.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelSuggestionTests {

    private final Person person = new Person("John Doe");
    private final Colonel<Person> colonel = new Colonel<>();

    @Test
    public void suggestFirstNodeInTree() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "");
        assertEquals(List.of(new Suggestion("foo")), suggestions);
    }

    @Test
    public void suggestMiddleNodeInTree() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo ");
        assertEquals(List.of(new Suggestion("bar")), suggestions);
    }

    @Test
    public void suggestLastNodeInTree() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar ");
        assertEquals(List.of(new Suggestion("baz")), suggestions);
    }

    //

    @Test
    public void suggestForFirstMultiNodesInTree() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();
        colonel.builder().path("fizz buzz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "");
        assertEquals(List.of(new Suggestion("foo"), new Suggestion("fizz")), suggestions);
    }

    @Test
    public void suggestForFirstMultiNodesInTreeWithPrefix() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();
        colonel.builder().path("fizz buzz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "f");
        assertEquals(List.of(new Suggestion("foo"), new Suggestion("fizz")), suggestions);

        suggestions = colonel.suggestions(person, "fo");
        assertEquals(List.of(new Suggestion("foo")), suggestions);

        suggestions = colonel.suggestions(person, "zap");
        assertEquals(List.of(), suggestions);
    }

    @Test
    public void suggestForMiddleMultiNodesInTree() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();
        colonel.builder().path("foo fizz baz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo ");
        assertEquals(List.of(new Suggestion("bar"), new Suggestion("fizz")), suggestions);
    }

    @Test
    public void suggestForMiddleMultiNodesInTreeWithPrefix() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();
        colonel.builder().path("foo fizz buzz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo fi");
        assertEquals(List.of(new Suggestion("fizz")), suggestions);

        suggestions = colonel.suggestions(person, "foo zap");
        assertEquals(List.of(), suggestions);
    }

    @Test
    public void suggestForLastMultiNodesInTree() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();
        colonel.builder().path("foo bar fizz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar ");
        assertEquals(List.of(new Suggestion("baz"), new Suggestion("fizz")), suggestions);
    }

    @Test
    public void suggestForLastMultiNodesInTreeWithPrefix() {
        colonel.builder().path("foo bar baz").executor(ctx -> {}).register();
        colonel.builder().path("foo bar fizz").executor(ctx -> {}).register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar b");
        assertEquals(List.of(new Suggestion("baz")), suggestions);

        suggestions = colonel.suggestions(person, "foo bar z");
        assertEquals(List.of(), suggestions);
    }

    //

    @Test
    public void suggestFirstArgument() {
        colonel.builder().path("foo bar")
                .string("p1", s -> null).completer("fizz", "buzz").done()
                .string("p2", s -> null).completer("hello", "world").done()
                .string("p3", s -> null).completer("hi", "mom").done()
                .executor(ctx -> {})
                .register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar ");
        assertEquals(List.of(new Suggestion("fizz"), new Suggestion("buzz")), suggestions);
    }

    @Test
    public void suggestFirstArgumentWithPrefix() {
        colonel.builder().path("foo bar")
                .string("p1", s -> null).completer("fizz", "buzz").done()
                .string("p2", s -> null).completer("hello", "world").done()
                .string("p3", s -> null).completer("hi", "mom").done()
                .executor(ctx -> {})
                .register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar buz");
        assertEquals(List.of(new Suggestion("buzz")), suggestions);

        suggestions = colonel.suggestions(person, "foo bar zap");
        assertEquals(List.of(), suggestions);
    }

    @Test
    public void suggestMiddleArgument() {
        colonel.builder().path("foo bar")
                .string("p1", s -> null).completer("fizz", "buzz").done()
                .string("p2", s -> null).completer("hello", "world").done()
                .string("p3", s -> null).completer("hi", "mom").done()
                .executor(ctx -> {})
                .register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar fizz ");
        assertEquals(List.of(new Suggestion("hello"), new Suggestion("world")), suggestions);
    }

    @Test
    public void suggestMiddleArgumentWithPrefix() {
        colonel.builder().path("foo bar")
                .string("p1", s -> null).completer("fizz", "buzz").done()
                .string("p2", s -> null).completer("hello", "world").done()
                .string("p3", s -> null).completer("hi", "mom").done()
                .executor(ctx -> {})
                .register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar fizz h");
        assertEquals(List.of(new Suggestion("hello")), suggestions);

        suggestions = colonel.suggestions(person, "foo bar fizz zap");
        assertEquals(List.of(), suggestions);
    }

    @Test
    public void suggestLastArgument() {
        colonel.builder().path("foo bar")
                .string("p1", s -> null).completer("fizz", "buzz").done()
                .string("p2", s -> null).completer("hello", "world").done()
                .string("p3", s -> null).completer("hi", "mom").done()
                .executor(ctx -> {})
                .register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar fizz hello ");
        assertEquals(List.of(new Suggestion("hi"), new Suggestion("mom")), suggestions);
    }

    @Test
    public void suggestLastArgumentWithPrefix() {
        colonel.builder().path("foo bar")
                .string("p1", s -> null).completer("fizz", "buzz").done()
                .string("p2", s -> null).completer("hello", "world").done()
                .string("p3", s -> null).completer("hi", "mom").done()
                .executor(ctx -> {})
                .register();

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar fizz hello mom");
        assertEquals(List.of(new Suggestion("mom")), suggestions);

        suggestions = colonel.suggestions(person, "foo bar fizz hello zap");
        assertEquals(List.of(), suggestions);
    }

}
