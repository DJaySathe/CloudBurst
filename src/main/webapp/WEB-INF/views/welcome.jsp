<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.dssathe.cloudburst.*"%>
<%@page import="org.springframework.security.core.Authentication"%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>

<%
String id = request.getParameter("userId");
String driverName = "com.mysql.jdbc.Driver";
String connectionUrl = "jdbc:mysql://localhost:3306/";
String dbName = "accounts";
String userId = "root";
String password = "root";

try {
Class.forName(driverName);
} catch (ClassNotFoundException e) {
e.printStackTrace();
}

Connection connection = null;
Statement statement = null;
ResultSet resultSet = null;
ResultSet resultSet1 = null;
int available = 4;
%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Welcome</title>

    <link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <script src="../../resources/js/Chart.bundle.js"></script>
    <script src="../../resources/js/utils.js"></script>
    <style>
        canvas {
            -moz-user-select: none;
            -webkit-user-select: none;
            -ms-user-select: none;
        }
    </style>
    <![endif]-->
</head>
<body>
<div class="container">

    <c:if test="${pageContext.request.userPrincipal.name != null}">
        <form id="logoutForm" method="POST" action="${contextPath}/logout">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>

        <h2>Welcome ${pageContext.request.userPrincipal.name} | <a onclick="document.forms['logoutForm'].submit()">Logout</a></h2>

        <form action="${contextPath}/reservation" class="form-reserve">
            <input type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
            <div class="form-group1">
                <button class="btn btn-lg btn-primary btn-block" type="new_reservation">New Reservation</button>
            </div>
        </form>

        <h4 class="text-center">My Reservations</a></h4>
        <table class="table table-striped table-bordered table-hover table-condensed" align="center" cellpadding="5" cellspacing="5" border="1">
        <tr>

        </tr>
        <tr >
        <td><b>id</b></td>
        <td><b>Image</b></td>
        <td><b>IP Address</b></td>
        <td><b>Username</b></td>
        <td><b>Password</b></td>
        </tr>
        <%
        try{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
        statement=connection.createStatement();
        String sql ="SELECT * FROM reservation WHERE Username='"+auth.getName().toString()+"';";
        System.out.println(sql);
        resultSet = statement.executeQuery(sql);

        while(resultSet.next()){
        %>
        <tr >

        <td><%=resultSet.getString("id") %></td>
        <td><%=resultSet.getString("Image") %></td>
        <td><%=resultSet.getString("IP Address") %></td>
        <td><%=resultSet.getString("Username") %></td>
        <td><%=resultSet.getString("Password") %></td>
        </tr>

        <%
        }
        } catch (Exception e) {
        e.printStackTrace();
        }
        %>
        </table>
    </c:if>

<!-- /container -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
<script src="../../resources/js/Chart.bundle.js"></script>
<script src="../../resources/js/utils.js"></script>
</body>
</html>
