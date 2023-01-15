package com.guflimc.colonel.common.test.util;

import java.util.ArrayList;
import java.util.List;

public class MockSource {

    private final String name;
    private int age = 17;
    private final List<String> permissions = new ArrayList<>();

    public MockSource(String name) {
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

    public String name() {
        return name;
    }

}