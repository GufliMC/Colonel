package com.guflimc.colonel.common.test;

import com.guflimc.colonel.common.Colonel;
import com.guflimc.colonel.common.build.Argument;
import com.guflimc.colonel.common.suggestion.Suggestion;
import com.guflimc.colonel.common.test.util.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelSuggestionTests {

    private final Person person = new Person("John Doe");
    private final Colonel<Person> colonel = new Colonel<>();

    @Test
    public void suggestLastNodeInTree() {
        colonel.register("foo bar", b -> b.executor(ctx -> {}));

        List<Suggestion> suggestions = colonel.suggestions(person, "foo ");
        assertEquals(List.of(new Suggestion("bar")), suggestions);
    }

}
