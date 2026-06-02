package com.innerpages.model;

public class PageItem {
    private final String name;
    private final String description;

    public PageItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
