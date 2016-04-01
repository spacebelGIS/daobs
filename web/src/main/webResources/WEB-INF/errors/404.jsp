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
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html ng-app="daobs">
<head lang="en">
  <meta charset="UTF-8">
  <title></title>
  <link rel="stylesheet"
        href="${webapp.rootUrl}assets/libs/bootstrap/css/bootstrap.min.css"/>
</head>
<body>
<nav class="navbar navbar-inverse navbar-static-top" role="navigation">
  <div class="container-fluid">
    <div class="collapse navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li>
          <a href="${webapp.rootUrl}">
            <i class="glyphicon glyphicon-home"></i>
          </a>
        </li>
      </ul>
    </div>
  </div>
</nav>
<div class="container-fluid">
  <div class="jumbotron">

    <h1>Oops, page not found!</h1>

    <p>This URL
      <strong>'${requestScope['javax.servlet.forward.request_uri']}'</strong>
      looks wrong.</p>

    <p>
      <a class="btn btn-primary btn-lg btn-block" role="button"
         href="${webapp.rootUrl}">
        <i class="glyphicon glyphicon-home"></i>&nbsp;Back to home page
      </a>
    </p>

  </div>
</div>
</body>
</html>
