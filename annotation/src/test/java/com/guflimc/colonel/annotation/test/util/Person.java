package com.guflimc.colonel.annotation.test.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Person {

    private final Queue<String> inbox = new ArrayDeque<>();

    private final String name;
    private int age;


    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name) {
        this(name, 0);
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

    public void send(String message) {
        inbox.add(message);
    }

    public String read() {
        return inbox.poll();
    }

}