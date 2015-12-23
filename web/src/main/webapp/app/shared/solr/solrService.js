(function () {
  "use strict";
  var app =  angular.module('solr');

  app.factory('solrService', ['$http', '$q', 'solrConfig',
    function ($http, $q, solrConfig) {
      function commit (core) {
        return $http.post(
          solrConfig.url + '/' +
          (core || solrConfig.core) +
          '/update/json',
          { 'commit': { 'waitSearcher': false }},
          {
            headers: { 'Content-type': 'application/json'}
          }
        );
      };
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
        'commit': commit,
        'delete': function (documentFilter, core) {
          var deferred = $q.defer();

          $http.post(
            solrConfig.url + '/' +
              (core || solrConfig.core) +
              '/update/json',
            { 'delete': { 'query': documentFilter }},
            {
                headers: { 'Content-type': 'application/json'}
              }
          ).then(function(data) {
            commit(core).then(function (data) {
              deferred.resolve(data);
            });
          }, function (data) {
            deferred.reject(data);
          });

          return deferred.promise;
        }
      };
    }]);
}());