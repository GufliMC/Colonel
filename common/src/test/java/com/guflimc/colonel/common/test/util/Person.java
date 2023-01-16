package com.guflimc.colonel.common.test.util;

import java.util.ArrayList;
import java.util.List;

public class Person {

    private final List<String> permissions = new ArrayList<>();
    private final String name;

    private int age = 17;
    private Gender gender;

    public Person(String name) {
        this.name = name;
    }

    //

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public void addPermission(String permission) {
        permissions.add(permission);
    }

    public void removePermission(String permission) {
        permissions.remove(permission);
    }

    //

    public int age() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    //

    public Gender gender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    //

    public String name() {
        return name;
    }

    //

    public enum Gender {
        MAN, WOMAN;
    }

}