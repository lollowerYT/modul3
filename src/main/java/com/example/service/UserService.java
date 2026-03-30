package com.example.service;

import com.example.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    // Файл для хранения пользователей – в каталоге Tomcat
    private static final String USERS_FILE = System.getProperty("catalina.base") + "/users.json";
    private static UserService instance;
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserService() {
        loadUsers();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try {
                User[] userArray = objectMapper.readValue(file, User[].class);
                for (User user : userArray) {
                    users.put(user.getLogin(), user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUsers() {
        try {
            objectMapper.writeValue(new File(USERS_FILE), users.values());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadUsers() {
        users.clear();
        loadUsers();
    }

    public synchronized boolean register(String login, String password, String email) {
        reloadUsers();                     // загружаем свежие данные
        if (users.containsKey(login)) {
            return false;                  // такой логин уже занят
        }
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(login, hashed, email);
        users.put(login, user);
        saveUsers();                       // сохраняем обновлённый список в JSON
        return true;
    }

    public synchronized User authenticate(String login, String password) {
        reloadUsers();
        User user = users.get(login);
        if (user == null) {
            return null;
        }
        boolean match = BCrypt.checkpw(password, user.getPasswordHash());
        return match ? user : null;
    }
}