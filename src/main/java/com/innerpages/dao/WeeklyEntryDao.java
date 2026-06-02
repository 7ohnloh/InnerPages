package com.innerpages.dao;

import com.innerpages.model.WeeklyEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WeeklyEntryDao {
    public List<WeeklyEntry> findByCategoryId(int categoryId) {
        List<WeeklyEntry> entries = new ArrayList<>();
        String sql = """
                SELECT id, category_id, title, created_at, updated_at
                FROM weekly_entries
                WHERE category_id = ?
                ORDER BY created_at, id
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entries.add(mapWeeklyEntry(resultSet));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load weekly entries", ex);
        }

        return entries;
    }

    public WeeklyEntry findById(int id) {
        String sql = "SELECT id, category_id, title, created_at, updated_at FROM weekly_entries WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapWeeklyEntry(resultSet);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load weekly entry", ex);
        }

        return null;
    }

    public boolean create(int categoryId, String title) {
        String sql = """
                INSERT INTO weekly_entries (category_id, title, created_at, updated_at)
                VALUES (?, ?, datetime('now'), datetime('now'))
                """;

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            statement.setString(2, title);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to create weekly entry", ex);
        }
    }

    public boolean rename(int id, String title) {
        String sql = "UPDATE weekly_entries SET title = ?, updated_at = datetime('now') WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setInt(2, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to rename weekly entry", ex);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM weekly_entries WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to delete weekly entry", ex);
        }
    }

    private WeeklyEntry mapWeeklyEntry(ResultSet resultSet) throws SQLException {
        return new WeeklyEntry(
                resultSet.getInt("id"),
                resultSet.getInt("category_id"),
                resultSet.getString("title"),
                resultSet.getString("created_at"),
                resultSet.getString("updated_at")
        );
    }
}
