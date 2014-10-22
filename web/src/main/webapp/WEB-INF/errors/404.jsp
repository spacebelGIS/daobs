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

            <h1>Oops, page not found!</h1>

            <p>This URL <strong>'${requestScope['javax.servlet.forward.request_uri']}'</strong> looks wrong.</p>

            <p>
                <a class="btn btn-primary btn-lg btn-block" role="button"
                        href="/solr/">
                <i class="glyphicon glyphicon-home"></i>&nbsp;Back to home page
                </a>
            </p>

        </div>
</div>
</body>
</html>