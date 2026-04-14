package com.example.service;

import com.example.model.User;
import com.example.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private static UserService instance;

    private UserService() {}

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

     //Регистрирует нового пользователя.
    public boolean register(String login, String password, String email) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Проверка существования
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class);
            query.setParameter("login", login);
            if (query.getSingleResult() > 0) {
                return false; // логин занят
            }

            // Хеширование пароля и создание пользователя
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(login, hashed, email);
            em.persist(user);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

     //Аутентификация пользователя.
    public User authenticate(String login, String password) {
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.login = :login", User.class);
            query.setParameter("login", login);
            User user = query.getSingleResult(); // может выбросить NoResultException

            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return user;
            }
            return null;
        } catch (NoResultException e) {
            return null; // пользователь не найден
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}