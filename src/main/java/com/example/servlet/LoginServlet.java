package com.example.servlet;

import com.example.model.User;
import com.example.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        User user = UserService.getInstance().authenticate(login, password);
        if (user != null) {
            req.getSession().setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/files");
        } else {
            req.setAttribute("error", "Неверный логин или пароль");
            req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
        }
    }
}