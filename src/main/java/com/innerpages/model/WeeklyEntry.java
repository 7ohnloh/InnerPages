package com.innerpages.model;

public class WeeklyEntry {
    private final int id;
    private final int categoryId;
    private final String title;
    private final String createdAt;
    private final String updatedAt;

    public WeeklyEntry(int id, int categoryId, String title, String createdAt, String updatedAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
