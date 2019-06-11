<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<c:if test="${not empty mealValue}">
    <jsp:useBean id="mealValue" type="ru.javawebinar.topjava.model.MealTo" scope="request"/>
</c:if>
<jsp:useBean id="dayNorm" type="java.lang.Integer" scope="request"/>
<html>
<head>
    <title>
        ${(not empty mealValue)?'[Редактор] Запись от ':'[Новая] Запись о потреблении пищи'}
        ${(not empty mealValue)?TimeUtil.formatTimeDate(mealValue.dateTime):''}
    </title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
</head>
<body>
<h3><a href="?">Список поступлений пиши</a></h3>
<hr>
<h2>${not empty mealValue?'[Редактор]':'[Новая запись]'} Поступление пиши</h2>
<c:if test="${not empty mealValue}">
    <h1>Исходное время: ${TimeUtil.formatTime(mealValue.time)}</h1>
    <h2>Исходная дата: ${TimeUtil.formatDate(mealValue.date)}</h2>
</c:if>
<hr/>
<form action="?" method="post">
    <input type="hidden" name="action" value="edit"/>
    <input type="hidden" name="id" value="${mealValue.id}"/>
    <table>
        <tr>
            <td style="width:20%">Дата:</td>
            <td><input type="date" name="date" value="${mealValue.date}"/></td>
        </tr>
        <tr>
            <td>Время:</td>
            <td><input type="time" name="time" value="${mealValue.time}"/></td>
        </tr>
        <tr>
            <td>Количество калорий:</td>
            <td><input type="number" name="calories" value="${mealValue.calories}"></td>
        </tr>
        <tr>
            <td>Примечание:</td>
            <td><textarea name="description">${mealValue.description}</textarea></td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="submit" value="Сохранить">
                <input type="reset" value="Отменить">
            </td>
        </tr>
    </table>
</form>
<c:if test="${not empty mealValue}">
    <a href="?action=view&id=${mealValue.id}">Посмотреть запись</a>
    <a href="?action=delete&id=${mealValue.id}">Удалить</a>
</c:if>
</body>
</html>