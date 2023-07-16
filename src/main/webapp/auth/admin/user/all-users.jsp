<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: James
  Date: 16/09/2022
  Time: 1:57 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>All Users</title>
    <link href="../../../css/styles.css" rel="stylesheet" type="text/css">
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
            background: rgba(110, 110, 110, 0.07)
        }
    </style>
</head>
<body>
<%@include file="/WEB-INF/jspf/navbar-admin.jspf" %>

<div class="centered_content_container">
    <h1>All User Details</h1>
        <table>
            <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Created</th>
                <th>Address</th>
            </tr>
            <c:forEach items="${users}" var="user">
                <tr>
                    <td>${user.username}</td>
                    <td>${user.email}</td>
                    <td>${user.ROLE}</td>
                    <td>${user.createTimestamp}</td>
                    <td>${user.address}</td>
                </tr>
            </c:forEach>
        </table>
</div>

</body>

</html>
