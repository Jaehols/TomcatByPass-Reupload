<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %><%--
  Created by IntelliJ IDEA.
  User: James
  Date: 14/09/2022
  Time: 10:03 am
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<html>

<head>
    <title>Landing Page</title>
    <link href="../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <p class="title">Welcome <sec:authentication property="name" />!</p>

    <br />
    <a href="listing/listing-all" class="button">Listings</a> <!-- TODO: Make authenticated?-->

    <br />
    <a href="sellergroup/seller-group-home" class="button">Seller Groups</a> <!-- TODO: Make authenticated?-->

    <br />
    <a href="orders/view-orders" class="button">Orders</a> <!-- TODO: Make authenticated?-->
    <br />
    <a href="user/user-details?uname=<sec:authentication property="principal.username"/>" class="button">My User Details</a>

    <br />
    <a href="listing/auctions" class="button">Followed Auctions</a>
    <br />

    <sec:authorize access="hasRole('ADMIN')">
        <a href="????????????" class="button">Users</a> <!-- TODO -->
        <br />
    </sec:authorize>
    <br />
</div>

</body>

</html>
