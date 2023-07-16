<%--
  Created by IntelliJ IDEA.
  User: James
  Date: 14/09/2022
  Time: 11:41 am
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<html>

<head>
    <title>User Home</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <p class="title">User Homepage</p>

    <sec:authorize access="hasRole('ADMIN')">
        <h2>Search for any User</h2><br/>
        <form action="user-details">
            Enter User Name: <input type="text" name="uname"><br>
            <input type="submit">
        </form>

        <h2>View All Users</h2><br/>
        <a href="../admin/user/all-users" class="button">All Users</a>
    </sec:authorize>

    <br/>
    <a href="user-details?uname=<sec:authentication property="principal.username"/>" class="button">My User Details</a>
    <br />
    <a hr="?" class="button">Edit User Details (TODO)</a>
    <br />
    <a href="../landing-page" class="button">Go Back</a>
    <br>
</div>

</body>

</html>
