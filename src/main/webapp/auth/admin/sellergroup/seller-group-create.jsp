<%--
  Created by IntelliJ IDEA.
  User: jamesvinnicombe
  Date: 13/9/2022
  Time: 3:07 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Create Seller Group</title>
    <link href="../../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar-admin.jspf" %>

<div class="centered_content_container">
    <p class="title">Create Seller Group</p>
    <br />
    <form action = "create-seller-group" method = "post">
        <table>
            <tr>
                <td>Seller Group Name</td>
                <td><input type = "text" name = "sg-name"></td>
            </tr>
        </table>
        <br />
        <input type = "submit" value = "Create Seller Group">
    </form>
</div>

</body>

</html>
