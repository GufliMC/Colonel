package com.guflimc.colonel.annotation.test;

import com.guflimc.colonel.annotation.AnnotationColonel;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.Parser;
import com.guflimc.colonel.annotation.annotations.parameter.Input;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.annotation.test.util.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenericsAnnotationsColonelTests {

    private final Person person = new Person("John Doe", 10);
    private final AnnotationColonel<Person> colonel = new AnnotationColonel<>(Person.class);

    @Test
    public void singleGenericParameter() {
        assertThrows(RuntimeException.class, () -> {
            colonel.registerAll(new Object() {
                @Command("addage")
                public <T> void addage(@Source Person person, @Parameter T amount) {
                    person.setAge(person.age() + (int) amount);
                }
            });
        });
    }

    @Test
    public void singleGenericParameterWrongNameParser() {
        assertThrows(RuntimeException.class, () -> {
            colonel.registerAll(new Object() {
                @Parser
                public int integer(String input) {
                    return Integer.parseInt(input);
                }

                @Command("addage")
                public <T> void addage(@Source Person person, @Parameter T amount) {
                    person.setAge(person.age() + (int) amount);
                }
            });
        });
    }

    @Test
    public void singleGenericParameterCorrectNameParser() {
        colonel.registerAll(new Object() {
            @Parser
            public int amount(@Input String input) {
                return Integer.parseInt(input);
            }

            @Command("addage")
            public <T> void addage(@Source Person person, @Parameter T amount) {
                person.setAge(person.age() + (int) amount);
            }
        });

        colonel.dispatch(person, "addage 5");
        assertEquals(15, person.age());
    }

    @Test
    public void singleGenericSourceNoMapper() {
        assertThrows(RuntimeException.class, () -> {
            colonel.registerAll(new Object() {
                @Command("addage")
                public <T> void addage(@Source Person person, @Source T amount) {
                    person.setAge(person.age() + (int) amount);
                }
            });
        });
    }

    @Test
    public void singleGenericSourceWithMapper() {
        colonel.registry().registerSourceMapper(Integer.class, "age", Person::age);

        colonel.registerAll(new Object() {
            @Command("addage")
            public <T> void addage(@Source T age, @Parameter int amount) {
                person.setAge((int) age + amount);
            }
        });
    }

    @Test
    public void singleGenericSourceWithMapperSpecificName() {
        colonel.registry().registerSourceMapper(Integer.class, "age", Person::age);

        colonel.registerAll(new Object() {
            @Command("addage")
            public <T> void addage(@Source("age") T num, @Parameter int amount) {
                person.setAge((int) num + amount);
            }
        });
    }

    @Test
    public void complexGenericSourceWithMapper() {
        colonel.registry().registerSourceMapper(Integer.class, "age", Person::age);

        colonel.registerAll(new Object() {
            @Command("addage")
            public <T extends Number & Comparable<T>> void addage(@Source T age, @Parameter int amount) {
                System.out.println(this.getClass().getDeclaredMethods()[0].getParameters()[0].getType().getName());
                person.setAge((Integer) age + amount);
            }
        });

        colonel.dispatch(person, "addage 5");
    }

}
