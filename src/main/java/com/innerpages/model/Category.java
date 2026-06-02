package com.innerpages.model;

public class Category {
    private final int id;
    private final String name;
    private final String createdAt;

    public Category(int id, String name, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
