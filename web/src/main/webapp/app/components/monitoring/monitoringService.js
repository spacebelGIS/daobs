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
  var app = angular.module('daobs');


  app.factory('monitoringService',
    ['$http', '$q', 'cfg', 'solrService',
      function ($http, $q, cfg, solrService) {
        return {
          loadMonitoring: function () {
            // TODO: Paging needed
            var rows = 10000, deferred = $q.defer();

            // TODO: Move to solrService
            $http.get(cfg.SERVICES.dataCore +
              '/select?q=documentType%3Amonitoring&' +
              'sort=reportingDate+desc,territory+asc&' +
              'start=0&rows=' + rows + '&' +
              'facet=true&facet.field=reportingYear&facet.field=territory&' +
              'wt=json').success(function (data) {
              var listOfMonitoring = data.response.docs,
                facets = data.facet_counts.facet_fields,
                facetArray = {};

              // Convert JSON encoded fields in response.
              angular.forEach(listOfMonitoring,
                function (monitoring, index) {
                  angular.forEach(monitoring.contact,
                    function (contact, contactIndex) {
                      listOfMonitoring[index].contact[contactIndex] =
                        angular.fromJson(contact);
                    });
                });
              angular.forEach(facets, function (facet, key) {
                var i = 0;
                facetArray[key] = [];
                // The facet response contains an array
                // with [value1, countFor1, value2, countFor2, ...]
                do {
                  // If it has records
                  if (facet[i + 1] > 0) {
                    facetArray[key].push({
                      label: facet[i],
                      count: facet[i + 1]
                    });
                  }
                  i = i + 2;
                } while (i < facet.length);
              });
              deferred.resolve({
                monitoring: listOfMonitoring,
                facet: facetArray
              });
            }).error(function (response) {
              deferred.reject(response);
            });

            return deferred.promise;
          },
          /**
           * Upload a monitoring.
           *
           * @param file
           * @param isOfficial Flag the monitoring as an official one
           * @param withRowData Index also row data section
           *  (only applies to INSPIRE monitoring)
           * @returns Array of promise
           */
          uploadMonitoring: function (files, isOfficial, withRowData) {
            var listOfDeffered = [],
              uploadUrl = cfg.SERVICES.reportingSubmit +
                '?commit=true&tr=inspire-monitoring-reporting' +
                (withRowData ? '-with-ai' : '') +
                '.xsl&isOfficial=' + isOfficial;

            angular.forEach(files, function (file) {
              var deferred = $q.defer();
              var fd = new FormData();
              fd.append('file', file);

              $http.post(uploadUrl, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
              }).success(function (data) {
                deferred.resolve(data);
              }).error(function (data) {
                deferred.reject(data);
              });

              listOfDeffered.push({file: file, promise: deferred.promise});
            });
            return listOfDeffered;
          },
          /**
           * Remove monitoring and related indicators.
           *
           * @param m MUST contains a reportingDate and territory which
           * is applied as filter. If undefined, all monitoring and
           * indicators are removed.
           *
           * @returns {*}
           */
          removeMonitoring: function (m) {
            // Remove indicator
            var filter = m === undefined ? '' :
              '+reportingDate:"' + m.reportingDate + '" ' +
              '+territory:"' + m.territory + '"',
              indicatorFilter = '+documentType:indicator ' + filter,
              monitoringFilter = '+documentType:monitoring* ' + filter,
              deferred = $q.defer();


            solrService.delete(indicatorFilter, cfg.SERVICES.dataCoreName).then(
              function () {
                // Remove monitoring
                solrService.delete(monitoringFilter, cfg.SERVICES.dataCoreName).then(
                  function (data) {
                    deferred.resolve('Monitoring deleted.');
                  },
                  function (response) {
                    deferred.reject(response);
                  });
              }
            );

            return deferred.promise;
          }
        };
      }]);
}());
