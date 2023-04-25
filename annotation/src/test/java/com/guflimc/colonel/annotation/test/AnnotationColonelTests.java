package com.guflimc.colonel.annotation.test;

import com.guflimc.colonel.annotation.AnnotationColonel;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.annotation.annotations.parameter.Parameter;
import com.guflimc.colonel.annotation.annotations.parameter.Source;
import com.guflimc.colonel.annotation.test.util.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationColonelTests {

    public static class TestCommandContainer {

        @Command("addage")
        @Command("age add")
        public void addage(@Source Person person, @Source int age, @Parameter int amount) {
            person.setAge(age + amount);
        }

    }

    //

    private final Person person = new Person("John Doe", 10);

    @Test
    public void test() {
        AnnotationColonel<Person> colonel = new AnnotationColonel<>();
        colonel.registerSourceMapper(Integer.class, Person::age);
        colonel.registerAll(new TestCommandContainer());

        colonel.dispatch(person, "addage 5");
        assertEquals(15, person.age());

        colonel.dispatch(person, "age add -5");
        assertEquals(10, person.age());
    }


}
