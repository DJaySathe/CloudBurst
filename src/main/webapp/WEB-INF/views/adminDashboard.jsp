<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.dssathe.cloudburst.*"%>

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
ResultSet resultSet0 = null;
ResultSet resultSet = null;
ResultSet resultSet1 = null;
int available = 0;
int max_available = 0;

try {
    connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
    statement=connection.createStatement();
    String sql0 ="SELECT count(*) as c FROM vm_info where availability = 1";
    resultSet0 = statement.executeQuery(sql0);
    resultSet0.next();
    available = resultSet0.getInt("c");

    sql0 ="SELECT count(*) as c FROM vm_info";
    resultSet0 = statement.executeQuery(sql0);
    resultSet0.next();
    max_available = resultSet0.getInt("c");
} catch (Exception e) {
    e.printStackTrace();
}
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

         <h4 class="text-center">VM Details</a></h4>
        <table class="table table-striped table-bordered table-hover table-condensed" align="center" cellpadding="5" cellspacing="5" border="1">
        <tr>

        </tr>
        <tr >
        <td><b>id</b></td>
        <td><b>name</b></td>
        <td><b>Availability</b></td>
        </tr>
        <%
        try{
        connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
        statement=connection.createStatement();
        String sql ="SELECT * FROM vm_info";

        resultSet = statement.executeQuery(sql);

        while(resultSet.next()){
        %>
        <tr >

        <td><%=resultSet.getString("id") %></td>
        <td><%=resultSet.getString("vm_id") %></td>
        <td><%=resultSet.getInt("availability")==1? "Available" : "Not Available"%></td>

        </tr>

        <%
        }
        statement=connection.createStatement();
        String sql1 ="SELECT COUNT(*) as c FROM vm_info where availability = 1";
        resultSet1 = statement.executeQuery(sql1);
        resultSet1.next();
        available = resultSet1.getInt("c");

        } catch (Exception e) {
        e.printStackTrace();
        }
        %>
        </table>
    </c:if>
    <div id="canvas-holder" style="width:40%">
             <canvas id="chart-area" />
    </div>

    <h4 class="text-center">Reservations Details</a></h4>
            <table class="table table-striped table-bordered table-hover table-condensed" align="center" cellpadding="5" cellspacing="5" border="1">
            <tr>
            </tr>
            <tr >
                <td><b>ID</b></td>
                <td><b>VM/AWS ID</b></td>
                <td><b>Image</b></td>
                <td><b>IP Address</b></td>
                <td><b>Username</b></td>
                <td><b>Start Time</b></td>
                <td><b>End Time</b></td>
            </tr>
            <%
            try{
                connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
                statement=connection.createStatement();
                String sql ="SELECT * FROM reservation;";
                resultSet = statement.executeQuery(sql);

                while(resultSet.next()) {
                    %>
                    <tr >
                    <td><%=resultSet.getString("id") %></td>
                    <td><%=resultSet.getString("vm_id") %></td>
                    <td><%=resultSet.getString("image_name") %></td>
                    <td><%=resultSet.getString("public_ip") %></td>
                    <td><%=resultSet.getString("username") %></td>
                    <td><%=resultSet.getString("start_time") %></td>
                    <td><%=resultSet.getString("end_time") %></td>
                    </tr>
                    <%
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            %>
            </table>

    <h4 class="text-center">User Details</a></h4>
    <table class="table table-striped table-bordered table-hover table-condensed" align="center" cellpadding="5" cellspacing="5" border="1">
            <tr>

            </tr>
            <tr >
            <td><b>id</b></td>
            <td><b>username</b></td>
            </tr>
            <%
            try{
            connection = DriverManager.getConnection(connectionUrl+dbName, userId, password);
            statement=connection.createStatement();
            String sql ="SELECT * FROM user u,user_role r where u.id = r.user_id and r.role_id =2";

            resultSet = statement.executeQuery(sql);

            while(resultSet.next()){
            %>
            <tr >

            <td><%=resultSet.getString("id") %></td>
            <td><%=resultSet.getString("username") %></td>

            </tr>
            <%
            }
            } catch (Exception e) {
            e.printStackTrace();
            }
            %>
            </table>
    <h4 class="text-center"><a href="${contextPath}/registration">Add new User</a></h4>
<!-- /container -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="${contextPath}/resources/js/bootstrap.min.js"></script>
<script src="../../resources/js/Chart.bundle.js"></script>
<script src="../../resources/js/utils.js"></script>
<script>
    var available = '<%= available%>';
    var max_available = '<%= max_available%>';
    var config = {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [
                    max_available-available,
                    available
                ],
                backgroundColor: [
                    window.chartColors.red,
                    window.chartColors.green
                ],
                label: 'Dataset 1'
            }],
            labels: [
                "Reserved",
                "Available"
            ]
        },
        options: {
            responsive: true,
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'Availability'
            },
            animation: {
                animateScale: true,
                animateRotate: true
            }
        }
    };
    window.onload = function() {
        var ctx = document.getElementById("chart-area").getContext("2d");
        window.myDoughnut = new Chart(ctx, config);
    };
    </script>
</body>
</html>
