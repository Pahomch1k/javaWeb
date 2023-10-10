<%--
  Created by IntelliJ IDEA.
  User: Pahomch1k
  Date: 2023-10-06
  Time: 14:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String pageBody = (String) request.getAttribute("page-body");
    String context = request.getContextPath();
%>
<!doctype>
<html>
<head>
    <title>Java web</title>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
</head>
<body>
<nav>
    <div class="nav-wrapper red darken-1">
        <a href="<%=context%>" class="brand-logo right ">Logo</a>
        <ul id="nav-mobile">
            <li><a href="<%=context%>/jsp">About JSP</a></li>
            <li <%= pageBody.equals( "filters.jsp") ? "class='active'" : ""%>>
                <a href="<%=context%>/filters">Filters</a>
            </li>
            <li <%= pageBody.equals( "ioc.jsp") ? "class='active'" : ""%>>
                <a href="<%=context%>/ioc">Ioc</a>
            </li>
        </ul>
    </div>
</nav>

<div class="container">
    <jsp:include page="<%= pageBody %>"/>
</div>

<footer class="page-footer red darken-1">
    <div class="container">
        <div class="row">
            <div class="col l6 s12">
                <h5 class="white-text">Your Company</h5>
                <p class="grey-text text-lighten-4">We are a team of college students working on this project like it's our full time job.</p>
            </div>
            <div class="col l4 offset-l2 s12">
                <h5 class="white-text">Links</h5>
                <ul>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 1</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 2</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 3</a></li>
                    <li><a class="grey-text text-lighten-3" href="#!">Link 4</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="footer-copyright">
        <div class="container">
            © 2023 Your Company
            <a class="grey-text text-lighten-4 right" href="#!">More Links</a>
        </div>
    </div>
</footer>

<script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
</body>
</html>
