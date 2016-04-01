/*
 * Copyright 2014-2016 European Environment Agency
 *
 * Licensed under the EUPL, Version 1.1 or – as soon
 * they will be approved by the European Commission -
 * subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
(function () {
  "use strict";
  var app = angular.module('solr');

  app.factory('solrService', ['$http', '$q', 'solrConfig',
    function ($http, $q, solrConfig) {
      function commit(core) {
        return $http.post(
          solrConfig.url + '/' +
          (core || solrConfig.core) +
          '/update/json',
          {'commit': {'waitSearcher': false}},
          {
            headers: {'Content-type': 'application/json'}
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
            {'delete': {'query': documentFilter}},
            {
              headers: {'Content-type': 'application/json'}
            }
          ).then(function (data) {
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
