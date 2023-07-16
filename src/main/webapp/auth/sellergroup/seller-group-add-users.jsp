<%--
  Created by IntelliJ IDEA.
  User: jamesvinnicombe
  Date: 19/9/2022
  Time: 3:29 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
    <title>Add Users</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <h1>Add Users to you Seller Group</h1>
    <form class="search-bar" method="post">
        <input type="search" name="query"/>
        <input type="hidden" name="sg_id" value="${sellerGroup.sgId}" />
        <input type="submit" value="Search Users" />
    </form>
    <table style="width: 15%">
        <c:forEach items="${appUsers}" var="appUsers">
            <form action="seller-group-manage" method="post">
                <tr>
                    <td>${appUsers.username}</td>
                    <td>
                        <input type="hidden" name="sg_id" value="${sellerGroup.sgId}" />
                        <input type="hidden" name="username_add" value="${appUsers.username}" />
                        <input type="submit" value="Add User">
                    </td>
                </tr>
            </form>
        </c:forEach>
    </table>
    <a></a>
</div>

</body>

</html>
