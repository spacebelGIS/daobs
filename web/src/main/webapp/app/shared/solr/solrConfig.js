(function () {
  "use strict";
  var app =  angular.module('solr');

  app.constant('solrConfig',
    {
      url: 'http://localhost:8983/',
      core: ''
    });
}());