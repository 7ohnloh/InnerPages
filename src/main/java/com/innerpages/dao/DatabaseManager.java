package com.innerpages.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DATABASE_DIRECTORY = "db";
    private static final String DATABASE_FILE = "innerpages.db";

    public static void initializeDatabase() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS pages (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, description TEXT)");
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        created_at TEXT
                    )
                    """);
            statement.executeUpdate("INSERT OR IGNORE INTO categories (name, created_at) VALUES ('School', datetime('now'))");
            statement.executeUpdate("INSERT OR IGNORE INTO categories (name, created_at) VALUES ('Exchange', datetime('now'))");
            statement.executeUpdate("INSERT OR IGNORE INTO categories (name, created_at) VALUES ('Personal', datetime('now'))");
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to initialize SQLite database", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        Path dbDirectory = Paths.get(System.getProperty("user.dir"), DATABASE_DIRECTORY);
        try {
            Files.createDirectories(dbDirectory);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to create database directory", ex);
        }
        String url = "jdbc:sqlite:" + dbDirectory.resolve(DATABASE_FILE).toAbsolutePath();
        return DriverManager.getConnection(url);
    }
}
