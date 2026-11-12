package com.library.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class SqlServerBootstrap {

    private SqlServerBootstrap() {
    }

    public static void ensureDatabase() {
        String url = System.getenv("SPRING_DATASOURCE_URL");
        if (url == null || !url.startsWith("jdbc:sqlserver:")) {
            return;
        }
        String username = System.getenv().getOrDefault("SPRING_DATASOURCE_USERNAME", "sa");
        String password = System.getenv().getOrDefault("SPRING_DATASOURCE_PASSWORD", "");
        String masterUrl = masterUrl(url);
        SQLException lastFailure = null;
        for (int attempt = 1; attempt <= 30; attempt++) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                try (Connection connection = DriverManager.getConnection(masterUrl, username, password);
                     Statement statement = connection.createStatement()) {
                    statement.execute("IF DB_ID(N'library') IS NULL CREATE DATABASE library");
                }
                return;
            } catch (ClassNotFoundException exception) {
                throw new IllegalStateException("SQL Server JDBC driver is not on the classpath", exception);
            } catch (SQLException exception) {
                lastFailure = exception;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while creating the library database", interrupted);
                }
            }
        }
        throw new IllegalStateException("Could not create the library database", lastFailure);
    }

    static String masterUrl(String url) {
        if (url.contains("databaseName=")) {
            return url.replaceAll("databaseName=[^;]*", "databaseName=master");
        }
        return url + ";databaseName=master";
    }
}
