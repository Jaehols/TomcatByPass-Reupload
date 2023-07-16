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
    <title>Viewing Followed Auction Listings </title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <div class="container">
        <h1>Your Followed Auction Listings</h1>
        <p>These are the auction listings that you have bid on.</p>
        <hr/>
        <c:if test="${feedback != null}">
            <p><b>${feedback}</b></p>
            <hr/>
        </c:if>
        <c:forEach items="${listings}" var="listing">
            <%--@elvariable id="bids" type="HashMap<UUID, Bid>"--%>
            <%--@elvariable id="bids_usernames" type="HashMap<UUID, String>"--%>
            <%--@elvariable id="listing_has_order" type="HashMap<UUID, Boolean>"--%>
            <%--@elvariable id="listing_ended" type="HashMap<UUID, Boolean>"--%>
            <div class="listing-body">
                <h3>Auction Listing <c:if test="${listing_ended.get(listing.listingId)}">(Ended)</c:if></h3>
                <p>Description: <br/><i>${listing.description}</i></p>
                <p>Ends: ${listing.endTimestamp}</p>
                <p>Highest Bid:<br/>
                    $${bids.get(listing.listingId).value}
                    by ${bids_usernames.get(listing.listingId)}
                    <b>${bids_usernames.get(listing.listingId) == username ? " (You)" : ""}</b>
                </p>
                <a href="view?listing_id=${listing.listingId}">View Listing</a>
                <c:if
                        test="${
                        !listing_has_order.get(listing.listingId)
                        && listing_ended.get(listing.listingId)
                        && bids_usernames.get(listing.listingId) == username
                        }"
                >
                    <hr/>
                    <div class="auction_order">
                        <h4>Congratulations! You won this auction. Place an Order now:</h4>
                        <form method="POST">
                            <input type="hidden" name="listing_id" value="${listing.listingId}"/>
                            <label><input type="checkbox" name="use_default_address" checked/>Use Default Address?</label><br/>
                            <p>If you don't want to use your default address, fill in your address below.</p>
                            <label>Address: <input type="text" name="address" value=""/></label><br/><br/>
                            <input type="submit" value="Place Order"/>
                        </form>
                    </div>
                </c:if>
            </div>
            <hr/>
        </c:forEach>
    </div>
</div>

</body>

</html>
