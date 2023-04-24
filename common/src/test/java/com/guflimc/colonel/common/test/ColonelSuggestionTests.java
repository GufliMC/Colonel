package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.suggestion.Suggestion;
import com.guflimc.colonel.common.test.util.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelSuggestionTests {

    private final Person person = new Person("John Doe");
    private final Colonel<Person> colonel = new Colonel<>();

    @Test
    public void suggestFirstNodeInTree() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "");
        assertEquals(List.of(new Suggestion("foo")), suggestions);
    }

    @Test
    public void suggestMiddleNodeInTree() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "foo ");
        assertEquals(List.of(new Suggestion("bar")), suggestions);
    }

    @Test
    public void suggestLastNodeInTree() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar ");
        assertEquals(List.of(new Suggestion("baz")), suggestions);
    }

    //

    @Test
    public void suggestForFirstMultiNodesInTree() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));
        colonel.register("fizz buzz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "");
        assertEquals(List.of(new Suggestion("foo"), new Suggestion("fizz")), suggestions);
    }

    @Test
    public void suggestForFirstMultiNodesInTreeWithPrefix() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));
        colonel.register("fizz buzz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "f");
        assertEquals(List.of(new Suggestion("foo"), new Suggestion("fizz")), suggestions);

        suggestions = colonel.suggestions(person, "fo");
        assertEquals(List.of(new Suggestion("foo")), suggestions);

        suggestions = colonel.suggestions(person, "zap");
        assertEquals(List.of(), suggestions);
    }

    @Test
    public void suggestForMiddleMultiNodesInTree() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));
        colonel.register("foo fizz baz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "foo ");
        assertEquals(List.of(new Suggestion("bar"), new Suggestion("fizz")), suggestions);
    }

    @Test
    public void suggestForMiddleMultiNodesInTreeWithPrefix() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));
        colonel.register("foo fizz buzz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "foo fi");
        assertEquals(List.of(new Suggestion("fizz")), suggestions);

        suggestions = colonel.suggestions(person, "foo zap");
        assertEquals(List.of(), suggestions);
    }

    @Test
    public void suggestForLastMultiNodesInTree() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));
        colonel.register("foo bar fizz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar ");
        assertEquals(List.of(new Suggestion("baz"), new Suggestion("fizz")), suggestions);
    }

    @Test
    public void suggestForLastMultiNodesInTreeWithPrefix() {
        colonel.register("foo bar baz", b -> b.executor(ctx -> {}));
        colonel.register("foo bar fizz", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "foo bar b");
        assertEquals(List.of(new Suggestion("baz")), suggestions);

        suggestions = colonel.suggestions(person, "foo bar z");
        assertEquals(List.of(), suggestions);
    }

}
