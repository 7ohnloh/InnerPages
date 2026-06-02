package com.innerpages.dao;

import com.innerpages.model.SearchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchDao {
    public List<SearchResult> searchComments(String query) {
        List<SearchResult> results = new ArrayList<>();
        String sql = """
                SELECT categories.id AS category_id,
                       categories.name AS category_name,
                       weekly_entries.id AS weekly_entry_id,
                       weekly_entries.title AS weekly_entry_title,
                       comments.id AS comment_id,
                       comments.content AS comment_content
                FROM comments
                JOIN weekly_entries ON comments.weekly_entry_id = weekly_entries.id
                JOIN categories ON weekly_entries.category_id = categories.id
                WHERE LOWER(comments.content) LIKE LOWER(?)
                ORDER BY categories.name, weekly_entries.title, comments.created_at, comments.id
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + query + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(new SearchResult(
                            resultSet.getInt("category_id"),
                            resultSet.getString("category_name"),
                            resultSet.getInt("weekly_entry_id"),
                            resultSet.getString("weekly_entry_title"),
                            resultSet.getInt("comment_id"),
                            resultSet.getString("comment_content")
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to search comments", ex);
        }

        return results;
    }
}
