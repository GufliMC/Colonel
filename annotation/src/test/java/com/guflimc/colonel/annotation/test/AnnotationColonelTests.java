package com.guflimc.colonel.annotation.test;

import com.guflimc.colonel.annotation.AnnotationColonel;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.Completer;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.annotation.test.util.Person;
import com.guflimc.colonel.common.build.CommandContext;
import com.guflimc.colonel.common.suggestion.Suggestion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnnotationColonelTests {

    private final Person person = new Person("John Doe", 10);
    private final AnnotationColonel<Person> colonel = new AnnotationColonel<>(Person.class);

    @Test
    public void singleParameter() {
        colonel.registerAll(new Object() {
            @Command("addage")
            public void addage(@Source Person person, @Parameter int amount) {
                person.setAge(person.age() + amount);
            }
        });

        colonel.dispatch(person, "addage 5");
        assertEquals(15, person.age());
    }

    @Test
    public void singleParameterTypeSourceMapper() {
        colonel.registry().registerSourceMapper(Integer.class, Person::age);
        colonel.registerAll(new Object() {
            @Command("addage")
            public void addage(@Source Person person, @Source int age, @Parameter int amount) {
                person.setAge(age + amount);
            }
        });

        colonel.dispatch(person, "addage 5");
        assertEquals(15, person.age());
    }

    @Test
    public void singleParameterFailTypeSourceMapper() {
        colonel.registry().registerSourceMapper(Integer.class, Person::age);

        assertThrows(Exception.class, () -> colonel.registerAll(new Object() {
            @Command("addage")
            public void addage(@Source Person person, @Source("unknown") int age, @Parameter int amount) {
                person.setAge(age + amount);
            }
        }));
    }

    @Test
    public void singleParameterNameSourceMapper() {
        colonel.registry().registerSourceMapper(Integer.class, "age", Person::age);

        colonel.registerAll(new Object() {
            @Command("addage")
            public void addage(@Source Person person, @Source("age") int age, @Parameter int amount) {
                person.setAge(age + amount);
            }
        });

        colonel.dispatch(person, "addage 5");
        assertEquals(15, person.age());
    }

    @Test
    public void singleParameterFailNameSourceMapper() {
        assertThrows(Exception.class, () -> {
            colonel.registerAll(new Object() {
                @Command("addage")
                public void addage(@Source Person person, @Source boolean something, @Parameter int amount) {
                    person.setAge(person.age() + amount);
                }
            });
        });
    }

    @Test
    public void singleParameterWithStandardParser() {
        colonel.registerAll(new Object() {

            @Parser("number")
            public int numberParser(CommandContext<Person> ctx, String input) {
                return Integer.parseInt(input) * 2;
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter(parser = "number") int amount) {
                person.setAge(person.age() + amount);
            }
        });

        colonel.dispatch(person, "addage 5");
        assertEquals(20, person.age());
    }

    @Test
    public void singleParameterWithLessArgumentsParser() {
        colonel.registerAll(new Object() {

            @Parser("number")
            public int numberParser(String input) {
                return Integer.parseInt(input) * 2;
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter(parser = "number") int amount) {
                person.setAge(person.age() + amount);
            }
        });

        colonel.dispatch(person, "addage 5");
        assertEquals(20, person.age());
    }

    @Test
    public void singleParameterWithNoArgumentsParser() {
        colonel.registerAll(new Object() {

            @Parser("number")
            public int numberParser() {
                return 20;
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter(parser = "number") int amount) {
                person.setAge(person.age() + amount);
            }
        });

        colonel.dispatch(person, "addage 5");
        assertEquals(30, person.age());
    }

    @Test
    public void singleParameterWithMoreArgumentsParser() {
        assertThrows(Exception.class, () -> {
            colonel.registerAll(new Object() {

                @Parser("number")
                public int numberParser(int x, String y, Object z) {
                    return 20;
                }

                @Command("addage")
                public void addage(@Source Person person, @Parameter(parser = "number") int amount) {
                    person.setAge(person.age() + amount);
                }
            });
        });
    }

    @Test
    public void singleParameterWithStandardCompleter() {
        colonel.registerAll(new Object() {

            @Completer("number")
            public List<Integer> numberCompleter(CommandContext<Person> ctx, String input) {
                return List.of(0, 5, 10, 15, 20, 25, 30);
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter(completer = "number") int amount) {
                person.setAge(person.age() + amount);
            }
        });

        List<Suggestion> suggestions = colonel.suggestions(person, "addage 2");
        assertEquals(List.of(new Suggestion("20"), new Suggestion("25")), suggestions);
    }

    @Test
    public void singleParameterWithLessArgumentsCompleter() {
        colonel.registerAll(new Object() {

            @Completer("number")
            public List<Integer> numberCompleter(String input) {
                return List.of(1, 2, 3);
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter(completer = "number") int amount) {
                person.setAge(person.age() + amount);
            }
        });

        List<Suggestion> suggestions = colonel.suggestions(person, "addage ");
        assertEquals(List.of(new Suggestion("1"), new Suggestion("2"), new Suggestion("3")), suggestions);
    }

    @Test
    public void singleParameterWithNoArgumentsCompleter() {
        colonel.registry().registerSourceMapper(Integer.class, "age", Person::age);
        colonel.registerAll(new Object() {

            @Completer("number")
            public List<Integer> numberCompleter() {
                return List.of(0, 5, 10, 15, 20, 25, 30);
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter(completer = "number") int amount) {
                person.setAge(person.age() + amount);
            }
        });

        List<Suggestion> suggestions = colonel.suggestions(person, "addage 1");
        assertEquals(List.of(new Suggestion("10"), new Suggestion("15")), suggestions);
    }

    @Test
    public void singleParameterWithMoreArgumentsCompleter() {
        colonel.registry().registerSourceMapper(Integer.class, Person::age);
        assertThrows(Exception.class, () -> {
            colonel.registerAll(new Object() {

                @Completer("number")
                public List<Integer> numberCompleter(int x, String y, Object z) {
                    return List.of(0, 5, 10, 15, 20, 25, 30);
                }

                @Command("addage")
                public void addage(@Source Person person, @Parameter(completer = "number") int amount) {
                    person.setAge(person.age() + amount);
                }
            });
        });
    }

    @Test
    public void singleParameterWithCustomCompleterWithSourceMapper() {
        colonel.registry().registerSourceMapper(Integer.class, Person::age);
        colonel.registerAll(new Object() {

            @Completer("number")
            public List<Integer> numberCompleter(@Source int age) {
                return List.of(age + 1, age + 2, age + 3);
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter(completer = "number") int amount) {
                person.setAge(person.age() + amount);
            }
        });

        List<Suggestion> suggestions = colonel.suggestions(person, "addage ");
        assertEquals(List.of(new Suggestion("11"), new Suggestion("12"), new Suggestion("13")), suggestions);
    }

    @Test
    public void singleParameterWithCustomParserWithSourceMapper() {
        colonel.registry().registerSourceMapper(Integer.class, Person::age);
        colonel.registerAll(new Object() {

            @Parser("number")
            public int numberParser(@Source int age, String input) {
                return Integer.parseInt(input) + age;
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter(parser = "number") int amount) {
                person.setAge(person.age() + amount);
            }
        });

        colonel.dispatch(person, "addage 10");
        assertEquals(30, person.age());
    }

    @Test
    public void singleLiteralOverload() {
        colonel.registry().registerSourceMapper(Integer.class, Person::age);
        colonel.registerAll(new Object() {

            @Command("addage")
            public void addage(@Source Person person) {
                person.setAge(person.age() + 1);
            }

            @Command("addage")
            public void addage(@Source Person person, @Parameter int amount) {
                person.setAge(person.age() + amount);
            }
        });

        colonel.dispatch(person, "addage");
        assertEquals(11, person.age());

        colonel.dispatch(person, "addage 4");
        assertEquals(15, person.age());
    }

    @Test
    public void multiLiteralOverload() {
        colonel.registry().registerSourceMapper(Integer.class, Person::age);
        colonel.registerAll(new Object() {

            @Command("add age")
            public void addage(@Source Person person) {
                person.setAge(person.age() + 1);
            }

            @Command("add age")
            public void addage(@Source Person person, @Parameter int amount) {
                person.setAge(person.age() + amount);
            }
        });

        colonel.dispatch(person, "add age");
        assertEquals(11, person.age());

        colonel.dispatch(person, "add age 9");
        assertEquals(20, person.age());
    }
}
