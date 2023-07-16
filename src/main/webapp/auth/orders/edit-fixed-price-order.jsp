<%--
  Created by IntelliJ IDEA.
  User: James
  Date: 18/09/2022
  Time: 4:38 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<html>

<head>
    <title>Edit Order</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <h1>Edit Order</h1>

    <form action="edit-fixed-price-order" method="post">
        <table>
            <tr>
                <td>Order ID</td>
                <td>${order.orderId}<br></td>
            </tr>
            <tr>
                <td>Description</td>
                <td>${listing.description}<br></td>
            </tr>
            <tr>
                <td>Created Time</td>
                <td>${order.createTimestamp}<br></td>
            </tr>
            <tr>
                <td>Address</td>
                <td><input type="text" name="address" placeholder="${order.address}" required><br></td>
            </tr>
            <tr>
                <td>Quantity</td>
                <td><input type="number" min="1" max="${order.quantity + order.listing.quantity}" name="quantity" placeholder="${order.quantity}" required><br></td>
            </tr>
            <tr>
                <td>Total</td>
                <td>${order.total}<br></td>
            </tr>
        </table>
        <input type="hidden" name="requestType" value="edit">
        <input type="hidden" name="orderId" value="${order.orderId}">
        <input type="hidden" name="isSgRequest" value="false">
        <input type="submit">
    </form >

    <form action="edit-fixed-price-order" method="post">
        <input type="hidden" name="requestType" value="cancel">
        <input type="hidden" name="orderId" value="${order.orderId}">
        <input type="hidden" name="isSgRequest" value="false">
        <input type="submit" value="Cancel Order">
    </form>

    <sec:authorize access="hasRole('ADMIN')">
        <a href="../admin/orders/all-orders" class="button">Go Back</a> <!-- TODO -->
        <br />
    </sec:authorize>
    <sec:authorize access="hasRole('USER')">
        <a href="view-orders" class="button">Go Back</a> <!-- TODO -->
        <br />
    </sec:authorize>
</div>

</body>

</html>
