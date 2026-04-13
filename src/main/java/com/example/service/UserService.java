package com.example.service;

import com.example.model.User;
import com.example.util.DatabaseConfig;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserService {
    private static UserService instance;

    private UserService() {
        ensureTableExists();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    //Создаёт таблицу users, если её ещё нет.
    private void ensureTableExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "login VARCHAR(50) PRIMARY KEY," +
                "password_hash VARCHAR(255) NOT NULL," +
                "email VARCHAR(100) NOT NULL" +
                ")";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure users table", e);
        }
    }

     //Регистрирует нового пользователя.
    public synchronized boolean register(String login, String password, String email) {
        // Проверка существования логина
        String checkSql = "SELECT login FROM users WHERE login = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, login);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Хеширование пароля и вставка
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        String insertSql = "INSERT INTO users (login, password_hash, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, login);
            insertStmt.setString(2, hashed);
            insertStmt.setString(3, email);
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

     //Проверяет учётные данные пользователя.
    public synchronized User authenticate(String login, String password) {
        String sql = "SELECT login, password_hash, email FROM users WHERE login = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, hash)) {
                    return new User(
                            rs.getString("login"),
                            hash,
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}