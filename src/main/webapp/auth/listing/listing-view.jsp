<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Sable
  Date: 18/09/2022
  Time: 4:54 pm
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>View Listing</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <div class="container">
        <h1>Viewing a Listing</h1>
        <hr/>
        <%--@elvariable id="listing" type="com.unimelb.tomcatbypass.model.Listing"--%>
        <c:if test="${listing['class'].simpleName == 'AuctionListing'}">
            <%--@elvariable id="listing" type="com.unimelb.tomcatbypass.model.AuctionListing"--%>
            <div class="listing-body">
                <h3>Auction Listing</h3>
                <p>Condition: ${listing.condition}</p>
                <p>Listed On: ${listing.createTimestamp}</p>
                <p>Ends: ${listing.endTimestamp}</p>
                <p>Start Price: $${listing.startPrice}</p>
                <p>Description: <br/>${listing.description}</p>
                <hr/>
                <c:if test="${bid_found}">
                    <h4>Highest Bid: $${bid.value} by ${bidder_username}</h4>
                </c:if>
                <c:if test="${!auction_over}">
                    <form action="view?listing_id=${listing.listingId}" method="POST">
                        <label>Place a Bid: <input type="number" min="${bid_found ? bid.value + 0.01 : listing.startPrice}" step="0.01" name="value"/></label>
                        <input type="submit"/>
                    </form>
                </c:if>
                <c:if test="${feedback != null}">
                    <hr/>
                    <p><b>${feedback}</b></p>
                </c:if>
            </div>
            <hr/>
            <%--@elvariable id="bid" type="com.unimelb.tomcatbypass.model.Bid"--%>
        </c:if>
        <c:if test="${listing['class'].simpleName == 'FixedListing'}">
            <%--@elvariable id="listing" type="com.unimelb.tomcatbypass.model.FixedListing"--%>
            <div class="listing-body">
                <h3>Fixed Price Listing</h3>
                <p>Condition: ${listing.condition}</p>
                <p>Listed On: ${listing.createTimestamp}</p>
                <p>Price: ${listing.price}</p>
                <p>Remaining Stock: ${listing.quantity}</p>
                <p>Description: <br/>${listing.description}</p>
                <c:if test="${listing.quantity > 0}">
                    <hr/>
                    <h4>Order this item:</h4>
                    <form action="view?listing_id=${listing.listingId}" method="POST">
                        <label>Quantity: <input type="number" min="1" max="${listing.quantity}" name="quantity" value="1"/></label><br/><br/><br/>
                        <label><input type="checkbox" name="use_default_address" checked/>Use Default Address?</label><br/>
                        <p>If you don't want to use your default address, fill in your address below.</p>
                        <label>Address: <input type="text" name="address" value=""/></label><br/><br/>
                        <input type="submit"/>
                    </form>
                </c:if>
                <c:if test="${feedback != null}">
                    <hr/>
                    <p><b>${feedback}</b></p>
                </c:if>
            </div>
            <hr/>
        </c:if>
    </div>
</div>

</body>

</html>
