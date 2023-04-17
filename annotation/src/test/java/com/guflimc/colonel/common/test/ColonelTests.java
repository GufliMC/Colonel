package com.guflimc.colonel.common.test;

import com.guflimc.colonel.annotation.Colonel;
import com.guflimc.colonel.annotation.annotations.Command;
import com.guflimc.colonel.common.annotation.parameter.Parameter;
import com.guflimc.colonel.common.annotation.parameter.Source;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColonelTests {

    public static class TestCommandContainer {

        @Command("addage")
        @Command("age add")
        public void addage(@Source TestEntity entity, @Source int age, @Parameter int amount) {
            entity.age = age + amount;
        }

    }

    public static class TestEntity {
        public final String name;
        public int age;

        private TestEntity(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    public void test() {
        Colonel<TestEntity> colonel = new Colonel<>();
        colonel.registry().registerSourceType(Integer.class, ctx -> ((TestEntity) ctx.source()).age);
        colonel.register(new TestCommandContainer());

        TestEntity entity = new TestEntity("test", 10);
        colonel.dispatcher().dispatch(entity, "addage 5");
        assertEquals(15, entity.age);

        colonel.dispatcher().dispatch(entity, "age add -5");
        assertEquals(10, entity.age);
    }


}
