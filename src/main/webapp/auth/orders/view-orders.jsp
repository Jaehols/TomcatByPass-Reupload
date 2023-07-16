<%--
  Created by IntelliJ IDEA.
  User: James
  Date: 18/09/2022
  Time: 2:01 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
    <title>All Orders</title>
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
    <h1>My Orders</h1>

    <h2>Fixed Price Orders</h2>

    <c:forEach items="${fixedOrders}" var="forder">
        <table style="width: 40%">
            <tr>
                <td><b>Description</b></td>
                <td>${descriptions.get(forder.orderId)}</td>
            </tr>
            <tr>
                <td><b>Created</b></td>
                <td>${forder.createTimestamp}</td>
            </tr>
            <tr>
                <td><b>Address</b></td>
                <td>${forder.address}</td>
            </tr>
            <tr>
                <td><b>Quantity</b></td>
                <td>${forder.quantity}</td>
            </tr>
            <tr>
                <td><b>Total</b></td>
                <td>${forder.total}</td>
            </tr>
        </table>
        <a href="edit-fixed-price-order?orderId=${forder.orderId}&sg=false" class="button">Edit this order</a> <br/><br/>
    </c:forEach>

    <h2>Auction Orders</h2>

    <c:forEach items="${auctionOrders}" var="aorder">
        <table style="width: 40%">
            <tr>
                <td><b>Description</b></td>
                <td>${descriptions.get(aorder.orderId)}</td>
            </tr>
            <tr>
                <td><b>Created</b></td>
                <td>${aorder.createTimestamp}</td>
            </tr>
            <tr>
                <td><b>Address</b></td>
                <td>${aorder.address}</td>
            </tr>
        </table>
        <a href="edit-auction-order?orderId=${aorder.orderId}&sg=false" class="button">Edit this order</a> <br/><br/>
    </c:forEach>
</div>

</body>

</html>
