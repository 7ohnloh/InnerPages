package com.innerpages.model;

public class SearchResult {
    private final int categoryId;
    private final String categoryName;
    private final int weeklyEntryId;
    private final String weeklyEntryTitle;
    private final int commentId;
    private final String commentContent;

    public SearchResult(int categoryId, String categoryName, int weeklyEntryId, String weeklyEntryTitle, int commentId, String commentContent) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.weeklyEntryId = weeklyEntryId;
        this.weeklyEntryTitle = weeklyEntryTitle;
        this.commentId = commentId;
        this.commentContent = commentContent;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getWeeklyEntryId() {
        return weeklyEntryId;
    }

    public String getWeeklyEntryTitle() {
        return weeklyEntryTitle;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getCommentContent() {
        return commentContent;
    }
}
