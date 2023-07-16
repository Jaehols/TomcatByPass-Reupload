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
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <h1>Seller Group Orders</h1>

    <h2>Fixed Price Orders</h2>

    <c:forEach items="${fixedOrders}" var="forder">
        OrderID: ${forder.orderId} <br/>
        Listing Description: ${descriptions.get(forder.orderId)} <br/>
        Created Time: ${forder.createTimestamp} <br/>
        Address: ${forder.address} <br/>
        Quantity ${forder.quantity} <br/>
        Total: ${forder.total} <br/>
        <a href="edit-fixed-price-order?orderId=${forder.orderId}&sg=true" class="button">Edit this order</a> <br/><br/>
    </c:forEach>


    <h2>Auction Orders</h2>

    <c:forEach items="${auctionOrders}" var="aorder">
        OrderID: ${aorder.orderId} <br/>
        Listing Description: ${descriptions.get(aorder.orderId)} <br/>
        Created Time: ${aorder.createTimestamp} <br/>
        Address: ${aorder.address} <br/>
        <a href="edit-auction-order?orderId=${aorder.orderId}&sg=true" class="button">Edit this order</a> <br/><br/>
    </c:forEach>
</div>

</body>

</html>
