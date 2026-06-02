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
            migrateWeeklyEntriesTable(connection);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS comments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        weekly_entry_id INTEGER NOT NULL,
                        content TEXT NOT NULL,
                        created_at TEXT,
                        updated_at TEXT,
                        FOREIGN KEY (weekly_entry_id) REFERENCES weekly_entries(id) ON DELETE CASCADE
                    )
                    """);
            statement.executeUpdate("INSERT OR IGNORE INTO categories (name, created_at) VALUES ('School', datetime('now'))");
            statement.executeUpdate("INSERT OR IGNORE INTO categories (name, created_at) VALUES ('Exchange', datetime('now'))");
            statement.executeUpdate("INSERT OR IGNORE INTO categories (name, created_at) VALUES ('Personal', datetime('now'))");
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to initialize SQLite database", ex);
        }
    }

    private static void migrateWeeklyEntriesTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            if (!weeklyEntriesTableExists(statement)) {
                createWeeklyEntriesTable(statement);
                return;
            }

            if (!weeklyEntriesNeedsMigration(statement)) {
                return;
            }

            statement.executeUpdate("PRAGMA foreign_keys = OFF");
            createWeeklyEntriesTable(statement, "weekly_entries_new");
            statement.executeUpdate("""
                    INSERT INTO weekly_entries_new (id, category_id, title, created_at, updated_at)
                    SELECT id,
                           category_id,
                           title,
                           COALESCE(created_at, datetime('now')),
                           COALESCE(updated_at, created_at, datetime('now'))
                    FROM weekly_entries
                    """);
            statement.executeUpdate("DROP TABLE weekly_entries");
            statement.executeUpdate("ALTER TABLE weekly_entries_new RENAME TO weekly_entries");
            statement.executeUpdate("PRAGMA foreign_keys = ON");
        }
    }

    private static boolean weeklyEntriesTableExists(Statement statement) throws SQLException {
        try (var resultSet = statement.executeQuery("""
                SELECT name
                FROM sqlite_master
                WHERE type = 'table' AND name = 'weekly_entries'
                """)) {
            return resultSet.next();
        }
    }

    private static boolean weeklyEntriesNeedsMigration(Statement statement) throws SQLException {
        boolean hasWeekStart = false;
        boolean createdAtNotNull = false;
        boolean updatedAtNotNull = false;

        try (var resultSet = statement.executeQuery("PRAGMA table_info(weekly_entries)")) {
            while (resultSet.next()) {
                String columnName = resultSet.getString("name");
                boolean notNull = resultSet.getInt("notnull") == 1;
                if ("week_start".equals(columnName) || "entry_date".equals(columnName) || "journal_date".equals(columnName)) {
                    hasWeekStart = true;
                } else if ("created_at".equals(columnName)) {
                    createdAtNotNull = notNull;
                } else if ("updated_at".equals(columnName)) {
                    updatedAtNotNull = notNull;
                }
            }
        }

        return hasWeekStart || !createdAtNotNull || !updatedAtNotNull;
    }

    private static void createWeeklyEntriesTable(Statement statement) throws SQLException {
        createWeeklyEntriesTable(statement, "weekly_entries");
    }

    private static void createWeeklyEntriesTable(Statement statement, String tableName) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS %s (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL,
                    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
                )
                """.formatted(tableName));
    }

    public static Connection getConnection() throws SQLException {
        Path dbDirectory = Paths.get(System.getProperty("user.dir"), DATABASE_DIRECTORY);
        try {
            Files.createDirectories(dbDirectory);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to create database directory", ex);
        }
        String url = "jdbc:sqlite:" + dbDirectory.resolve(DATABASE_FILE).toAbsolutePath();
        Connection connection = DriverManager.getConnection(url);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("PRAGMA foreign_keys = ON");
        }
        return connection;
    }
}
