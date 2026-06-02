package com.innerpages.dao;

import com.innerpages.model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {
    public List<Comment> findByWeeklyEntryId(int weeklyEntryId) {
        List<Comment> comments = new ArrayList<>();
        String sql = """
                SELECT id, weekly_entry_id, content, created_at, updated_at
                FROM comments
                WHERE weekly_entry_id = ?
                ORDER BY created_at, id
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, weeklyEntryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    comments.add(new Comment(
                            resultSet.getInt("id"),
                            resultSet.getInt("weekly_entry_id"),
                            resultSet.getString("content"),
                            resultSet.getString("created_at"),
                            resultSet.getString("updated_at")
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load comments", ex);
        }

        return comments;
    }

    public boolean create(int weeklyEntryId, String content) {
        String sql = """
                INSERT INTO comments (weekly_entry_id, content, created_at, updated_at)
                VALUES (?, ?, datetime('now'), datetime('now'))
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, weeklyEntryId);
            statement.setString(2, content);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to create comment", ex);
        }
    }

    public boolean update(int id, String content) {
        String sql = "UPDATE comments SET content = ?, updated_at = datetime('now') WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, content);
            statement.setInt(2, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to update comment", ex);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM comments WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to delete comment", ex);
        }
    }
}
