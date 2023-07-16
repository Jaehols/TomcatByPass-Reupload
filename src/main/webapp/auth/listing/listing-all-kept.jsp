<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: sable
  Date: 18/9/22
  Time: 1:30pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>View All Listings</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <div class="container">
        <h1>Browse the TomcatBypass Marketplace</h1>
        <p>Browse auctions and fixed-price listings posted by sellers on TomcatBypass.</p>
        <hr/>
        <form class="search-bar">
            <label>Search Listings <input type="search" name="query" value="${param.query}"/></label>
            <input type="submit"/>
        </form>
        <hr/>
        <c:forEach items="${listings}" var="listing">
            <c:if test="${listing['class'].simpleName == 'AuctionListing'}">
                <div class="listing-body">
                    <h3>Auction Listing</h3>
                    <p>Condition: ${listing.condition}</p>
                    <p>Listed On: ${listing.createTimestamp}</p>
                    <p>Ends: ${listing.endTimestamp}</p>
                    <p>Start Price: $${listing.startPrice}</p>
                    <p>Description: <br/>${listing.description}</p>
                    <a href="view?listing_id=${listing.listingId}">View Listing</a>
                </div>
                <hr/>
            </c:if>

            <c:if test="${listing['class'].simpleName == 'FixedListing'}">
                <div class="listing-body">
                    <h3>Fixed Price Listing</h3>
                    <p>Condition: ${listing.condition}</p>
                    <p>Listed On: ${listing.createTimestamp}</p>
                    <p>Price: ${listing.price}</p>
                    <p>Remaining Stock: ${listing.quantity}</p>
                    <p>Description: <br/>${listing.description}</p>
                    <a href="view?listing_id=${listing.listingId}">View Listing</a>
                </div>
                <hr/>
            </c:if>
        </c:forEach>
    </div>
</div>

</body>

</html>
