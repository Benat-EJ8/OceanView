package com.oceanview.resort.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DataSourceProvider {
    private static volatile DataSourceProvider instance;
    private String driverClass;
    private String jdbcUrl;
    private String username;
    private String password;

    private DataSourceProvider() {
        loadConfig();
    }

    public static DataSourceProvider getInstance() {
        if (instance == null) {
            synchronized (DataSourceProvider.class) {
                if (instance == null) {
                    instance = new DataSourceProvider();
                }
            }
        }
        return instance;
    }

    private void loadConfig() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                driverClass = "org.postgresql.Driver";
                jdbcUrl = "jdbc:postgresql://localhost:5432/oceanview";
                username = "postgres";
                password = "postgres";
                try {
                    Class.forName(driverClass);
                } catch (ClassNotFoundException ignored) {
                }
                return;
            }
            Properties p = new Properties();
            p.load(in);
            driverClass = p.getProperty("jdbc.driver", "org.postgresql.Driver");
            jdbcUrl = p.getProperty("jdbc.url", "jdbc:postgresql://localhost:5432/oceanview");
            username = p.getProperty("jdbc.username", "postgres");
            password = p.getProperty("jdbc.password", "postgres");
            Class.forName(driverClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DB config", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return java.sql.DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
