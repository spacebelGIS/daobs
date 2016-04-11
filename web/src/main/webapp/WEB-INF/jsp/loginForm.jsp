<%--

Copyright 2014-2016 European Environment Agency

Licensed under the EUPL, Version 1.1 or – as soon
they will be approved by the European Commission -
subsequent versions of the EUPL (the "Licence");
You may not use this work except in compliance
with the Licence.
You may obtain a copy of the Licence at:

https://joinup.ec.europa.eu/community/eupl/og_page/eupl

Unless required by applicable law or agreed to in
writing, software distributed under the Licence is
distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied.
See the Licence for the specific language governing
permissions and limitations under the Licence.

--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html ng-app="login">
<head lang="en">
  <meta charset="UTF-8">
  <title>Daobs</title>
  <link rel="stylesheet" href="<spring:url value="/assets/libs/intro.js/introjs.min.css"/>"/>
  <link rel="stylesheet" href="<spring:url value="/assets/libs/bootstrap/css/bootstrap.min.css"/>"/>
  <link rel="stylesheet" href="<spring:url value="/assets/libs/font-awesome/css/font-awesome.min.css"/>"/>
  <link rel="stylesheet" href="<spring:url value="/assets/libs/angular-ui-notification.min.css"/>"/>
  <link rel="stylesheet" href="<spring:url value="/assets/css/main.css"/>"/>
  <link rel="stylesheet" href="<spring:url value="/assets/css/login.css"/>"/>
</head>
<body data-ng-controller="LoginController"
      data-ng-cloak=""
      data-translate-cloak="">


<div class="container-fluid">
  <form class="form-signin" method="post" action="/login">
    <h2 class="form-signin-heading" data-translate>login.pleaseSignIn</h2>
    <c:if test="${param.error ne null}">
      <div class="alert alert-danger" data-translate="">login.userPassword-error</div>
    </c:if>
    <label for="username" class="sr-only" data-translate>login.username</label>
    <input type="text" name="username" id="username" class="form-control" placeholder="{{'login.username-placeholder' | translate}}" required autofocus>
    <label for="inputPassword" class="sr-only" data-translate>login.password</label>
    <input type="password" name="password" id="inputPassword" class="form-control" placeholder="{{'login.password-placeholder' | translate}}" required>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <button class="btn btn-lg btn-primary btn-block" type="submit" value="Login">Sign in</button>
  </form>
</div>

<nav class="navbar navbar-default navbar-bottom">
  <div class="container">
    <em data-translate="">licence</em>
  </div>
</nav>
<script src="<spring:url value="/assets/libs/jquery/jquery-2.1.1.min.js"/>"></script>

<script src="<spring:url value="/assets/libs/moment.min.js"/>"></script>

<script src="<spring:url value="/assets/libs/angular/angular.min.js"/>" language="JavaScript"></script>
<script src="<spring:url value="/assets/libs/angular/angular-route.min.js"/>"
        language="JavaScript"></script>
<script src="<spring:url value="/assets/libs/angular/angular-route.min.js"/>"
        language="JavaScript"></script>
<script src="<spring:url value="/assets/libs/angular/angular-translate.min.js"/>"
        language="JavaScript"></script>
<script src="<spring:url value="/assets/libs/angular/angular-translate-loader-static-files.min.js"/>"
        language="JavaScript"></script>

<script src="<spring:url value="/assets/libs/bootstrap/js/bootstrap.min.js"/>"></script>
<script src="<spring:url value="/assets/libs/intro.js/intro.min.js"/>"></script>
<script src="<spring:url value="/assets/libs/angular-ui-notification.min.js"/>"></script>

<script src="<spring:url value="/app/app.config.js"/>"></script>
<script src="<spring:url value="/app/login.module.js"/>"></script>
<script src="<spring:url value="/app/components/login/loginController.js"/>"></script>


</body>
</html>
