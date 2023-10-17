<%@ page import="step.learning.dto.models.RegFormModel" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    RegFormModel model = (RegFormModel) request.getAttribute("reg-model");
    String loginValue = model == null ? "" : model.getLogin();
    String nameValue = model == null ? "" : model.getName();
    String emailValue = model == null ? "" : model.getEmail();
    String birthdateValue = model == null ? "" : model.setBirthdateAsString();
    Map<String, String> errors = model == null ? new HashMap<String, String>() : (HashMap<String, String>) model.getErrorMessages();
    String nameClass = model == null ? "validate" : (errors.containsKey("name") ? "invalid" : "valid");
    String loginClass = model == null ? "validate" : (errors.containsKey("login") ? "invalid" : "valid");
    String regMessage = (String) request.getAttribute("reg-mess");
    if (regMessage == null) {
        regMessage = "";
    }
%>

<h2>Регистрация</h2>
<p><%= regMessage%></p>
<div class="row">
    <form class="col s12" method="post" action="" enctype="multipart/form-data">
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">badge</i>
                <input value="<%=loginValue%>" name="reg-login" id="reg-login" type="text" class="<%=nameClass%>">
                <label for="reg-login">Логин</label>
                <% if( errors.containsKey("login")) { %>
                <span class="helper-text" data-error="wrong"><%=errors.get("login")%>></span>
                <% } %>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">person</i>
                <input value="<%=nameValue%>" name="reg-name" id="reg-name" type="text" class="<%=loginClass%>">
                <label for="reg-name">Имя</label>
                <% if( errors.containsKey("name")) { %>
                    <span class="helper-text" data-error="wrong"><%=errors.get("name")%>></span>
                <% } %>
            </div>
        </div>

        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">lock</i>
                <input value="123" name="reg-pass" id="reg-pass"type="password" class="validate">
                <label for="reg-pass">Пароль</label>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">lock_open</i>
                <input value="123" name="reg-repeat" id="reg-repeat" type="password" class="validate">
                <label for="reg-repeat">Повторить пароль</label>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">alternate_email</i>
                <input value="<%=emailValue%>" name="reg-email" id="reg-email" type="email" class="validate">
                <label for="reg-email">Емали</label>
            </div>
            <div class="input-field col s6">
                <i class="material-icons prefix">cake</i>
                <input value="<%=birthdateValue%>" name="reg-birthdate" id="reg-birthdate" type="date" class="validate">
                <label for="reg-birthdate">Дата рождения</label>
            </div>
        </div>
        <div class="row">
            <div class="input-field col s6">
                <i class="material-icons prefix">receipt_long</i>
                <label> &emsp;
                    <input name="reg-rules" id="reg-rules" type="checkbox" class="filled-in validate">
                    <span>все будет норм <span>
                </label>
            </div>

            <div class="file-field input-field col s6">
                 <div class="btn">
                     <i class="material-icons">account_box</i>
                    <input type="file" name="reg-avatar">
                 </div>
                 <div class="file-path-wrapper">
                    <input class="file-path validate" type="text" placeholder="Аватарка">
                 </div>
            </div>

        </div>
        <div class="input-field row right-align">
            <button class="waves-effect waves-light btn red darken-3"><i class="material-icons right">how_to_reg</i>Регистрация</button>
        </div>
    </form>
</div>

