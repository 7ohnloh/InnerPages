package com.innerpages.dao;

import com.innerpages.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name, created_at FROM categories ORDER BY id";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("created_at")
                ));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load categories", ex);
        }

        return categories;
    }

    public boolean create(String name) {
        String sql = "INSERT OR IGNORE INTO categories (name, created_at) VALUES (?, datetime('now'))";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to create category", ex);
        }
    }
}
