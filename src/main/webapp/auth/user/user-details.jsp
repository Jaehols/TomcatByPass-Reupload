<%--
  Created by IntelliJ IDEA.
  User: James Hollingsworth
  Date: 9/09/2022
  Email: jhollingswor@student.unimelb.edu.au
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>${name}</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
            background: rgba(110, 110, 110, 0.07)
        }
    </style>
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <p class="title"> Your Details </p>
    <table>
        <tr>
            <td><b>Username</b></td>
            <td>${username}</td>
        </tr>
        <tr>
            <td><b>Email</b></td>
            <td>${email}</td>
        </tr>
        <tr>
            <td><b>Address</b></td>
            <td>${address}</td>
        </tr>
        <tr>
            <td><b>Role</b></td>
            <td>${role}</td>
        </tr>
        <tr>
            <td><b>Created</b></td>
            <td>${createtimestamp}</td>
        </tr>
    </table>
</div>

</body>

</html>
