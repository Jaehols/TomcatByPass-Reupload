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
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
        }
    </style>
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div style="display: flex; justify-content: center; flex-direction: column">
    <div class="horizontal_centered_container">
        <c:if test="${isFixed == true}">
            <h1>Fixed Listings</h1>
        </c:if>
        <c:if test="${isFixed == false}">
            <h1>Auction Listings</h1>
        </c:if>
    </div>

    <div class="horizontal_centered_container">
        <div>
            <div>
                <a href="listing-user-all">View My Listings</a>
            </div>
            <div>
                <a href="fixed-listing-create">Create Fixed Listing</a>
            </div>
            <div>
                <a href="auction-listing-create">Create Auction Listing</a>
            </div>
        </div>
    </div>

    <br />

    <div class="horizontal_centered_container">
        <c:if test="${isFixed == true}">
            <table style="width: 70%">
                <tr>
                    <th>Description</th>
                    <th>Condition</th>
                    <th>Quantity</th>
                    <th>Price</th>
                    <th>Listed On</th>
                </tr>
                <c:forEach items="${listings}" var="listing">
                    <tr>
                        <td><a href="view?listing_id=${listing.listingId}">${listing.description}<a/></td>
                        <td>${listing.condition}</td>
                        <td>${listing.quantity}</td>
                        <td>${listing.price}</td>
                        <td>${listing.createTimestamp}</td>
                    </tr>
                </c:forEach>
            </table>
            <br />
        </c:if>

        <c:if test="${isFixed == false}">
            <table style="width: 60%">
                <tr>
                    <th>Description</th>
                    <th>Condition</th>
                    <th>Start Price</th>
                    <th>Listed On</th>
                    <th>Ends On</th>
                </tr>
                <c:forEach items="${listings}" var="listing">
                    <tr>
                        <td><a href="view?listing_id=${listing.listingId}">${listing.description}<a/></td>
                        <td>${listing.condition}</td>
                        <td>${listing.startPrice}</td>
                        <td>${listing.createTimestamp}</td>
                        <td>${listing.endTimestamp}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </div>

    <br />

    <div class="horizontal_centered_container">
        <form action = "listing-all" method = "POST">
            <div class="horizontal_centered_container">
                <c:if test="${isFixed == true}">
                    <div>
                        <input type="radio" id="fixed-radio1" name="isFixed" value="true" checked>
                        <label for="fixed-radio1">Fixed</label>
                    </div>
                    <div>
                        <input type="radio" id="auction-radio1" name="isFixed" value="false">
                        <label for="auction-radio1">Auction </label>
                    </div>
                </c:if>

                <c:if test="${isFixed == false}">
                    <div>
                        <input type="radio" id="fixed-radio2" name="isFixed" value="true">
                        <label for="fixed-radio2">Fixed</label>
                    </div>
                    <div>
                        <input type="radio" id="auction-radio2" name="isFixed" value="false" checked>
                        <label for="auction-radio2">Auction </label>
                    </div>
                </c:if>
                <button type="submit" name="offset" value="${offset}">Reload</button>
            </div>

            <br />

            <c:set var="offset" scope="session" value="${offset}"/>
            <c:set var="limit" scope="session" value="${limit}"/>

            <div class="horizontal_centered_container">
                <c:if test="${offset - limit >= 0}">
                    <button type="submit" name="offset" value="${offset - limit}">Previous</button>
                </c:if>
                <c:if test="${listings.size() == limit}">
                    <button type="submit" name="offset" value="${offset + limit}">Next</button>
                </c:if>
            </div>

            <br />

            <div>
                <hr/>
                <label>Search Listings By Description: <input type="search" name="query" value="${param.query}"/></label>
                <input type="submit"/>
                <hr/>
            </div>
        </form>
    </div>

    <div class="horizontal_centered_container">
        <a href="search">Infinite Listing Search</a>
        <br />
    </div>

    <div class="horizontal_centered_container">
        <p>Showing Results In Range ${offset} - ${offset + limit}</p>
    </div>

    <div class="horizontal_centered_container">
        <p>Results Per Page: ${limit}</p>
    </div>
</div>

</body>

</html>
