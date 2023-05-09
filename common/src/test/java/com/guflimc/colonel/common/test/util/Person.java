package com.guflimc.colonel.common.test.util;

import java.util.ArrayDeque;
import java.util.Queue;

public class Person {

    private final Queue<String> inbox = new ArrayDeque<>();

    private final String name;
    private int age = 17;

    public Person(String name) {
        this.name = name;
    }

    //

    public int age() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    //

    public String name() {
        return name;
    }

    //

    public void send(Object message) {
        inbox.add(message.toString());
    }

    public String read() {
        return inbox.poll();
    }


}