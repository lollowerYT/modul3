<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.File" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.nio.charset.StandardCharsets" %>
<html>
<head>
    <title>Проводник по файлам</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        tr:hover { background-color: #f5f5f5; }
        .date { color: #666; font-size: 0.9em; margin-bottom: 10px; }
        .up-link { margin: 10px 0; }
    </style>
</head>
<body>

<div class="date">
    <b>Страница сгенерирована: <%= request.getAttribute("generatedTime") %></b>
</div>

<h2>Текущая директория: <%= request.getAttribute("currentPath") %></h2>

<%
    String parent = (String) request.getAttribute("parentPath");
    if (parent != null) {
        // Кодируем путь для ссылки (чтобы корректно передать в URL)
        String encodedParent = URLEncoder.encode(parent, StandardCharsets.UTF_8);
%>
    <div class="up-link">
        <a href="files?path=<%= encodedParent %>">⬆ Вверх</a>
    </div>
<%
    }
%>

<table>
    <tr>
        <th>Имя</th>
        <th>Размер</th>
        <th>Дата последнего изменения</th>
    </tr>

<%
    File[] files = (File[]) request.getAttribute("files");
    if (files != null) {
        for (File file : files) {
            // Кодируем абсолютный путь для ссылки
            String encodedPath = URLEncoder.encode(file.getAbsolutePath(), StandardCharsets.UTF_8);
            String name = file.getName();
            // Если это директория, добавим "/" для наглядности
            if (file.isDirectory()) {
                name += "/";
            }
%>
    <tr>
        <td>
            <a href="files?path=<%= encodedPath %>"><%= name %></a>
        </td>
        <td>
            <% if (file.isFile()) { %>
                <%= file.length() %> байт
            <% } else { %>
                &mdash;
            <% } %>
        </td>
        <td>
            <%= new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
                    .format(new java.util.Date(file.lastModified())) %>
        </td>
    </tr>
<%
        }
    } else {
%>
    <tr><td colspan="3">Директория пуста</td></tr>
<%
    }
%>

</table>

</body>
</html>