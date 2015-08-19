(function () {
  "use strict";
  var app =  angular.module('solr');

  app.constant('solrConfig',
    {
      url: '${solr.webapp.name}',
      core: ''
    });
}());