package com.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/files")
public class FileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Получаем путь из параметра запроса (возможно, с пробелами и спецсимволами)
        String pathParam = request.getParameter("path");
        String path;

        if (pathParam == null || pathParam.isEmpty()) {
            // Если параметр не задан, показываем домашнюю директорию пользователя
            path = System.getProperty("user.home");
        } else {
            // Декодируем URL (чтобы русские буквы и пробелы корректно обрабатывались)
            path = URLDecoder.decode(pathParam, StandardCharsets.UTF_8);
        }

        File currentFile = new File(path);

        // Проверяем существование
        if (!currentFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Путь не найден: " + path);
            return;
        }

        // Если это файл, отдаём его на скачивание
        if (currentFile.isFile()) {
            // Пытаемся определить MIME-тип, иначе application/octet-stream
            String mimeType = Files.probeContentType(currentFile.toPath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);
            // Указываем, что это вложение (скачивание)
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + currentFile.getName() + "\"");
            // Копируем содержимое файла в выходной поток
            Files.copy(currentFile.toPath(), response.getOutputStream());
            return;
        }

        // Получаем список файлов в директории
        File[] filesList = currentFile.listFiles();
        if (filesList != null) {
            // Сортируем по имени (папки и файлы вместе)
            Arrays.sort(filesList, Comparator.comparing(File::getName));
        }

        // Передаём данные в JSP
        request.setAttribute("files", filesList);
        request.setAttribute("currentPath", currentFile.getAbsolutePath());
        request.setAttribute("parentPath", currentFile.getParent());
        request.setAttribute("generatedTime",
                new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));

        // Пересылаем запрос на JSP-страницу
        request.getRequestDispatcher("/WEB-INF/files.jsp").forward(request, response);
    }
}