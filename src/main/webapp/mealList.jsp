<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<jsp:useBean id="mealsList" type="java.util.List" scope="request"/>
<html>
<head>
    <title>Покушали</title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
</head>
<body>
<h3><a href="index.html">Главное меню</a></h3>
<hr>
<h2>Покушали</h2>
<p>Дневная норма: ${dayNorm} калорий</p>
<table>
    <thead>
    <tr>
        <th>Дата</th>
        <th>Время</th>
        <th>Описание</th>
        <th>Калории</th>
        <th colspan="3"><a href="?action=add">ДОБАВИТЬ</a></th>
    </tr>
    </thead>
    <c:forEach items="${mealsList}" var="meal">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr${meal.excess?' class="excess"': ''}>
            <td>${TimeUtil.formatDate(meal.date)}</td>
            <td>${TimeUtil.formatTime(meal.time)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><div class="boolean-${meal.excess}"></div></td>
            <td><a href="?action=edit&id=${meal.id}">Изменить</a></td>
            <td><a href="?action=view&id=${meal.id}">Покрупнее</a></td>
            <td><a href="?action=delete&id=${meal.id}">Удалить</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>