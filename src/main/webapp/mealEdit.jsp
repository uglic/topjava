<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<jsp:useBean id="action" type="java.lang.String" scope="request"/>
<jsp:useBean id="returnUri" type="java.lang.String" scope="request"/>
<c:if test="${not empty mealValue}">
    <jsp:useBean id="mealValue" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
</c:if>
<html>
<head>
    <title>
        ${(not empty mealValue)?'[Редактор] Поступление от ':'[Новое] Поступление'}
        ${(not empty mealValue)?TimeUtil.DATETIME_FORMATTER.format(mealValue.dateTime):''}
    </title>
    <link rel="stylesheet" type="text/css" href="css/style.css"/>
</head>
<body>
<h3><a href="${returnUri}">Список</a></h3>
<hr>
<h2>${not empty mealValue?'[Редактор]':'[Новая запись]'} Поступление</h2>
<hr/>
<form action="?" method="post">
    <input type="hidden" name="action" value="${action}"/>
    <input type="hidden" name="id" value="${mealValue.id}"/>
    <table>
        <tr>
            <td>Дата:</td>
            <td><input type="datetime-local" name="dateTime" value="${mealValue.dateTime}"/></td>
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
    <a href="?action=delete&id=${mealValue.id}">Удалить</a>
</c:if>
<a href="${returnUri}">К списку</a>
</body>
</html>