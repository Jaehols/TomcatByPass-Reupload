<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: tom
  Date: 9/13/22
  Time: 8:22 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>View Listings</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
    <title>Title</title>
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
        }
    </style>
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <h1>All My Listings</h1>
    <div>
        <a href="listing-all">View All Listings</a>
    </div>
    <div>
        <a href="fixed-listing-create">Create Fixed Listing</a>
    </div>
    <div>
        <a href="auction-listing-create">Create Auction Listing</a>
    </div>
    <br />
    <c:catch var="exception">${errorMessage}</c:catch>
    <table style="width: 60%">
        <tr>
            <th>Description</th>
            <th>Condition</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Listed On</th>
            <th>Start Price</th>
            <th>Ends On</th>
            <th>REMOVE</th>
        </tr>
        <c:forEach items="${listings}" var="listing">
            <tr>
                <td><a href="view?listing_id=${listing.listingId}">${listing.description}<a/></td>
                <td>${listing.condition}</td>
                <c:if test="${listing['class'].simpleName == 'FixedListing'}">
                    <td>${listing.quantity}</td>
                    <td>${listing.price}</td>
                    <td>${listing.createTimestamp}</td>
                    <td>N/A</td>
                    <td>N/A</td>
                </c:if>
                <c:if test="${listing['class'].simpleName == 'AuctionListing'}">
                    <td>N/A</td>
                    <td>N/A</td>
                    <td>${listing.createTimestamp}</td>
                    <td>${listing.startPrice}</td>
                    <td>${listing.endTimestamp}</td>
                </c:if>
                <td><a href="listing-delete?listing_id=${listing.listingId}">REMOVE</a> </td>
            </tr>
        </c:forEach>
    </table>
    <br />

    <c:set var="offset" scope="session" value="${offset}"/>
    <c:set var="limit" scope="session" value="${limit}"/>

    <c:if test="${offset - limit >= 0}">
        <a href="?offset=${offset - limit}">Previous</a>
        <br />
    </c:if>
    <c:if test="${listings.size() >= limit}"> <!-- TODO: Improve this... In this case the actual limit = 2 * this one-->
        <a href="?offset=${offset + limit}">Next</a>
    </c:if>

    <div>
        <p>Showing Results In Range ${offset} - ${(offset + limit) * 2}</p>
    </div>
    <div>
        <p>Results Per Page: ${limit * 2}</p>
    </div>
    <br />
</div>

</body>

</html>
