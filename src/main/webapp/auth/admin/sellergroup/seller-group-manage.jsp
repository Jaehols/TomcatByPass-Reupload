<%--
  Created by IntelliJ IDEA.
  User: jamesvinnicombe
  Date: 18/9/2022
  Time: 10:27 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
    <title>Manage Seller Group</title>
    <link href="../../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar-admin.jspf" %>

<div class="centered_content_container">
    <p class="title">${sellerGroup.name}</p>
    <br />
    <a href="seller-group-add-users?sg_id=${sellerGroup.sgId}">Add Users</a>
    <br />
    <a href="../orders/all-orders">View Orders</a>
    <br />
    <div class="container">
        <table style="width: 100%">
            <tr>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Address</th>
            </tr>
            <c:forEach items="${appUsers}" var="appUsers">
                <form action="seller-group-manage" method="post">
                    <tr>
                        <td>${appUsers.username}</td>
                        <td>${appUsers.email}</td>
                        <td>${appUsers.ROLE}</td>
                        <td>${appUsers.address}</td>
                        <td>
                            <input type="hidden" name="sg_id" value="${sellerGroup.sgId}" />
                            <input type="hidden" name="username_del" value="${appUsers.username}" />
                            <input type="submit" value="Remove User">
                        </td>
                    </tr>
                </form>
            </c:forEach>
        </table>
    </div>
</div>

</body>

</html>
