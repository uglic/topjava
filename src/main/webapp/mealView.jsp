<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<jsp:useBean id="mealValue" type="ru.javawebinar.topjava.model.MealTo" scope="request"/>
<jsp:useBean id="dayNorm" type="java.lang.Integer" scope="request"/>
<html>
<head>
    <title>Запись от ${TimeUtil.formatTimeDate(mealValue.dateTime)}</title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
</head>
<body>
<h3><a href="?">Список поступлений пиши</a></h3>
<hr>
<h2>Поступление пиши</h2>

<h1>Время: ${TimeUtil.formatTime(mealValue.time)}</h1>
<h2>Дата: ${TimeUtil.formatDate(mealValue.date)}</h2>
<hr/>
<table>
    <tr>
        <td style="width:20%">Количество калорий:</td>
        <td>${mealValue.calories}</td>
    </tr>
    <tr>
        <td>Примечание:</td>
        <td>${mealValue.description}</td>
    </tr>
    <tr>
        <td colspan="2">&nbsp;</td>
    </tr>
    <tr>
        <td colspan="2" ${mealValue.excess?' class="excess"': ''}>Дневная норма: ${dayNorm} калорий</td>
    </tr>
</table>
<a href="?action=edit&id=${mealValue.id}">Изменить запись</a>
<a href="?action=delete&id=${mealValue.id}">Удалить</a>
</body>
</html>