<%--
  Created by IntelliJ IDEA.
  User: James
  Date: 11/09/2022
  Time: 12:56 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
    <title>Register</title>
    <link href="css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>

<div class="centered_content_container">
    <p class="title">Create a new user</p>
    <c:catch var="exception">${errorMessage}</c:catch>
    <form action="register" method="post">
        <table>
            <tr>
                <td>Email</td>
                <td><input type="text" name="email"><br></td>
            </tr>
            <tr>
                <td>Username</td>
                <td><input type="text" name="uname"><br></td>
            </tr>
            <tr>
                <td>Password</td>
                <td><input type="password" name="pwd"><br></td>
            </tr>
            <tr>
                <td>Address</td>
                <td><input type="text" name="address"><br></td>
            </tr>
        </table>
        <input type="submit">
    </form>
</div>

</body>

</html>
