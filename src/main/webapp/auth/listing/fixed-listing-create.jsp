<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: tom
  Date: 9/13/22
  Time: 4:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Create Fixed Listing</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <c:catch var="exception">${errorMessage}</c:catch>
    <form action = "fixed-listing-create" method = "POST">
<%--    <form action = "listing-user-all" method = "POST">--%>
        <table>
            <tr>
                <td><label for="description">Description</label></td>
                <td><input type="text" id="description" name="description"></td>
            </tr>
            <tr>
                <td>
                    <input type="radio" id="condition1" name="condition" value="NEW">
                    <label for="condition1">New</label>
                </td>
                <td>
                    <input type="radio" id="condition2" name="condition" value="USED">
                    <label for="condition2">Used</label>
                </td>
            </tr>
            <tr>
                <td><label for="price">Price</label></td>
                <td><input type="text" id="price" name="price"><br></td>
            </tr>
            <tr>
                <td><label for="quantity">Quantity</label></td>
                <td><input type="number" id="quantity" name="quantity"></td>
            </tr>
            <c:forEach items="${sellerGroups}" var="sellerGroups">
                <tr>
                    <td><input type="radio" id="${sellerGroups.sgId}" name="seller-group-uuid" value="${sellerGroups.sgId}"></td>
                    <td><label for="${sellerGroups.sgId}">Seller Group: ${sellerGroups.name}</label></td>
                </tr>
            </c:forEach>
        </table>
        <input type = "submit" value = "Submit" />
    </form>
</div>

</body>

</html>
