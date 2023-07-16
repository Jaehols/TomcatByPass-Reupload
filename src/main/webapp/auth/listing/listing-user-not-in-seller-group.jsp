<%--
  Created by IntelliJ IDEA.
  User: tom
  Date: 9/17/22
  Time: 10:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Sorry...</title>
    <link href="../../css/styles.css" rel="stylesheet" type="text/css">
</head>

<body>
<%@include file="/WEB-INF/jspf/navbar.jspf" %>

<div class="centered_content_container">
    <p class="title">Sorry, you must be in at least one seller group to create listings...</p>
    <br />
    <a href="${pageContext.request.contextPath}/auth/sellergroup/seller-group-create.jsp" class="button">Create a Seller Group</a>
    <br />
</div>

</body>

</html>
