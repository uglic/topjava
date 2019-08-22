<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav class="navbar navbar-dark bg-dark py-0">
    <div class="container">
        <a href="meals" class="navbar-brand"><img src="resources/images/icon-meal.png"> <spring:message code="app.title"/></a>
        <sec:authorize access="isAuthenticated()">
            <form:form class="form-inline my-2" action="logout" method="post">
                <sec:authorize access="hasRole('ROLE_ADMIN')">
                    <a class="btn btn-info mr-1" href="users"><spring:message code="user.title"/></a>
                </sec:authorize>
                <a class="btn btn-info mr-1" href="profile">${userTo.name} <spring:message code="app.profile"/></a>
                <button class="btn btn-primary my-1" type="submit">
                    <span class="fa fa-sign-out"></span>
                </button>
            </form:form>
            <c:set var="locationUrl" value="${pageContext.request.contextPath}"/>
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
            <form:form class="form-inline my-2" id="login_form" action="spring_security_check" method="post">
                <input class="form-control mr-1" type="text" placeholder="Email" name="username">
                <input class="form-control mr-1" type="password" placeholder="Password" name="password">
                <button class="btn btn-success" type="submit">
                    <span class="fa fa-sign-in"></span>
                </button>
            </form:form>
            <c:set var="locationUrl" value="login"/>
        </sec:authorize>
        <div class="dropdown">
            <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                ${fn:substring(pageContext.response.locale, 0, 2)}
            </button>
            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                <a class="dropdown-item" href="${locationUrl}?langu=en">English</a>
                <a class="dropdown-item" href="${locationUrl}?langu=ru">Русский</a>
            </div>
        </div>
    </div>
</nav>
