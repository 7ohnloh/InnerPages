package com.innerpages.model;

public class Comment {
    private final int id;
    private final int weeklyEntryId;
    private final String content;
    private final String createdAt;
    private final String updatedAt;

    public Comment(int id, int weeklyEntryId, String content, String createdAt, String updatedAt) {
        this.id = id;
        this.weeklyEntryId = weeklyEntryId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public int getWeeklyEntryId() {
        return weeklyEntryId;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
