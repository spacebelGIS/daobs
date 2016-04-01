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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html ng-app="daobs">
<head lang="en">
  <meta charset="UTF-8">
  <title></title>
  <link rel="stylesheet" href="/solr/lib/bootstrap/css/bootstrap.min.css"/>
</head>
<body>
<nav class="navbar navbar-inverse navbar-static-top" role="navigation">
  <div class="container-fluid">
    <div class="collapse navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <li>
          <a href="/solr/">
            <i class="glyphicon glyphicon-home"></i>
          </a>
        </li>
      </ul>
    </div>
  </div>
</nav>
<div class="container-fluid">
  <div class="jumbotron">

    <h1>Oops, something went wrong!</h1>

    <div class="alert alert-danger">
      Exception is:
      <strong>${pageContext.exception}</strong>
    </div>
    <p>
      <a class="btn btn-primary btn-lg btn-block" role="button"
         href="/solr/">
        <i class="glyphicon glyphicon-home"></i>&nbsp;Back to home page
      </a>
      <a class="btn btn-default btn-lg btn-block" role="button"
         href="${pageContext.errorData.requestURI}">
        <i class="glyphicon glyphicon-refresh"></i>&nbsp;Reload
      </a>
    </p>

    <h3>More details about the error</h3>

    <div class="alert alert-danger">

      Status
      code:&nbsp;<strong>${pageContext.errorData.statusCode}</strong><br/>
      URI:&nbsp;<strong>${pageContext.errorData.requestURI}</strong><br/>
      Trace:
      <code>
        <c:forEach var="stacktraceElement"
                   items="${pageContext.exception.stackTrace}">
          ${stacktraceElement}
        </c:forEach>
      </code>
    </div>
  </div>
</div>
</body>
</html>
