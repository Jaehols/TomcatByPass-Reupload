<%--
  Created by IntelliJ IDEA.
  User: jamesvinnicombe
  Date: 20/9/2022
  Time: 8:59 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>All Seller Groups</title>
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
    <p class="title">Seller Group Home</p>
    <a href="create-seller-group" class="button">Create Seller Group</a>
    <br/>
    <table style="width: 25%">
        <c:forEach items="${sellerGroups}" var="sellerGroup">
            <tr>
                <td>${sellerGroup.name}</td>
                <td><a href="seller-group-manage?sg_id=${sellerGroup.sgId}">View</a></td>
                <td><a href="seller-group-home?sg_id=${sellerGroup.sgId}">Delete</a></td>
            </tr>
        </c:forEach>
    <table/>
</div>
</body>
</html>
