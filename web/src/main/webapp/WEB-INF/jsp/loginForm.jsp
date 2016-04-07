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
      data-ng-cloak="">


<div class="container-fluid">
  <form class="form-signin">
    <h2 class="form-signin-heading">Please sign in</h2>
    <label for="inputEmail" class="sr-only">Email address</label>
    <input type="email" id="inputEmail" class="form-control" placeholder="Email address" required autofocus>
    <label for="inputPassword" class="sr-only">Password</label>
    <input type="password" id="inputPassword" class="form-control" placeholder="Password" required>
    <div class="checkbox">
      <label>
        <input type="checkbox" value="remember-me"> Remember me
      </label>
    </div>
    <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
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

<script src="<spring:url value="/app/login.module.js"/>"></script>
<script src="<spring:url value="/app/components/login/loginController.js"/>"></script>


</body>
</html>
