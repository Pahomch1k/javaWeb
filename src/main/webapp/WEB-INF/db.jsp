<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String connectionStatus = (String) request.getAttribute("connectionStatus");
%>

<h1>Работа с ДБ</h1>
<p>Connection Status: <%=connectionStatus%></p>

<br>

<h2>Упарвление данными</h2>

<p>
    <button id="db-create-button" class="waves-effect waves-light btn">
        <i class="material-icons right">cloud</i>
        button
    </button>

    <input name="user-name" placeholder="Имя">
    <input name="user-phone" placeholder="Телефон">

    <button id="db-insert-button" class="waves-effect waves-light btn">
        <i class="material-icons right">phone_iphone</i>
        Заказать звонок
    </button>

    <br>
    <u id="out"></u>
</p>

<div class="row">
    <button id="db-read-button" class="waves-effect waves-light btn">
        <i class="material-icons right">view_list</i>
        Посмотреть
    </button>
</div>