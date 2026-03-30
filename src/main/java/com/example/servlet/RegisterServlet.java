package com.example.servlet;

import com.example.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        boolean success = UserService.getInstance().register(login, password, email);
        if (success) {
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("error", "Пользователь с таким логином уже существует");
            req.getRequestDispatcher("/WEB-INF/register.jsp").forward(req, resp);
        }
    }
}