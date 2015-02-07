(function () {
  "use strict";
  var app =  angular.module('solr');

  app.factory('solrService', ['$http', 'solrConfig',
    function ($http, solrConfig) {
      return {
        ping: function (core) {
          return $http.get(solrConfig.url +
            (core || solrConfig.core) +
            '/ping?wt=json');
        },
        query: function (query, displayField) {
          return $http({
            method: 'JSONP',
            url: solrConfig.url,
            params: {
              'json.wrf': 'JSON_CALLBACK',
              'q': query,
              'fl': displayField
            }
          });
        },
        'delete': function (documentFilter, core) {
          return $http.post(
            solrConfig.url +
              (core || solrConfig.core) +
              '/update/json',
            { 'delete': { 'query': documentFilter }},
            {
                params: { commit: true },
                headers: { 'Content-type': 'application/json'}
              }
          );
        }
      };
    }]);
}());