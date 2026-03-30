package com.example.servlet;

import com.example.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

@WebServlet("/files")
public class FileServlet extends HttpServlet {

    // Корневая папка для всех домашних директорий пользователей
    private static final String BASE_DIR = System.getProperty("java.io.tmpdir") + File.separator + "filemanager" + File.separator;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String userHome = BASE_DIR + user.getLogin();

        // Создаём домашнюю папку, если её нет
        File userHomeDir = new File(userHome);
        if (!userHomeDir.exists()) {
            userHomeDir.mkdirs();
        }

        String pathParam = req.getParameter("path");
        String path;
        if (pathParam == null || pathParam.isEmpty()) {
            path = userHome;
        } else {
            path = URLDecoder.decode(pathParam, StandardCharsets.UTF_8);
        }

        File requestedFile = new File(path);

        // Проверка, что путь внутри домашней папки
        if (!requestedFile.getCanonicalPath().startsWith(new File(userHome).getCanonicalPath())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Доступ запрещён");
            return;
        }

        if (!requestedFile.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Путь не найден: " + path);
            return;
        }

        // Если это файл – отдаём на скачивание
        if (requestedFile.isFile()) {
            String mimeType = Files.probeContentType(requestedFile.toPath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            resp.setContentType(mimeType);
            resp.setHeader("Content-Disposition",
                    "attachment; filename=\"" + requestedFile.getName() + "\"");
            Files.copy(requestedFile.toPath(), resp.getOutputStream());
            return;
        }

        // Получаем список файлов в директории
        File[] filesList = requestedFile.listFiles();
        if (filesList != null) {
            Arrays.sort(filesList, Comparator.comparing(File::getName));
        }

        // Передаём данные в JSP
        req.setAttribute("files", filesList);
        req.setAttribute("currentPath", requestedFile.getAbsolutePath());
        req.setAttribute("parentPath", requestedFile.getParent());
        req.setAttribute("generatedTime",
                new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));

        req.getRequestDispatcher("/WEB-INF/files.jsp").forward(req, resp);
    }
}