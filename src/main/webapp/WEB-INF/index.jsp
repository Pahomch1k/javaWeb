<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>qweqwe</h1>
<p>
    <%
        String str = "hello";
        str += " world";
        int x = 10;
    %>
</p>
<p>
    str = <%=str%>, x + 5 = <%=x + 5%>
</p>

<ul>
    <% for (int i = 0; i < 5; i++) { %>
            <li>
                Item No <%=i + 1%>
            </li>
    <% } %>
</ul>
<jsp:include page="fragment.jsp"/>
</body>
</html>
