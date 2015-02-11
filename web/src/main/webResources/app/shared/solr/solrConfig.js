(function () {
  "use strict";
  var app =  angular.module('solr');

  app.constant('solrConfig',
    {
      url: '${webapp.rootUrl}',
      core: ''
    });
}());