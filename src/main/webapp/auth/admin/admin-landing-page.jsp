<%--
  Created by IntelliJ IDEA.
  User: James
  Date: 19/09/2022
  Time: 8:47 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<html>

<head>
    <title>Admin Landing Page</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar-admin.jspf" %>

<div class="centered_content_container">
    <p class="title">Welcome <sec:authentication property="name" />!</p>
    <br />
    <a href="listing/all-listings" class="button">Listings</a>
    <br />
    <a href="sellergroup/seller-group-home" class="button">Seller Groups</a>
    <br />
    <a href="orders/all-orders" class="button">Orders</a>
    <br />
    <a href="user/all-users" class="button">Users</a>
</div>

</body>

</html>
