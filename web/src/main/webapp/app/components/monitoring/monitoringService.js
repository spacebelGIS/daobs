(function () {
  "use strict";
  var app = angular.module('daobs');


  app.factory('monitoringService',
    ['$http', '$q', 'cfg', 'solrService',
      function ($http, $q, cfg, solrService) {
        return {
          loadMonitoring : function () {
            // TODO: Paging needed
            var rows = 10000, deferred = $q.defer();

            // TODO: Move to solrService
            $http.get(cfg.SERVICES.dataCore +
              '/select?q=documentType%3Amonitoring&' +
              'sort=reportingDate+desc,territory+asc&' +
              'start=0&rows=' + rows + '&' +
              'facet=true&facet.field=reportingYear&facet.field=territory&' +
              'wt=json').
              success(function (data) {
                var listOfMonitoring = data.response.docs,
                  facets = data.facet_counts.facet_fields,
                  facetArray = {};

                // Convert JSON encoded fields in response.
                angular.forEach(listOfMonitoring,
                  function(monitoring, index) {
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
           * Remove monitoring and related indicators.
           *
           * @param m MUST contains a reportingDate and territory which
           * is applied as filter. If undefined, all monitoring and
           * indicators are removed.
           *
           * @returns {*}
           */
          removeMonitoring : function (m) {
            // Remove indicator
            var filter = m === undefined ? '' :
                  '+reportingDate:"' + m.reportingDate + '" ' +
                  '+territory:"' + m.territory + '"',
              indicatorFilter = '+documentType:indicator ' + filter,
              monitoringFilter = '+documentType:monitoring ' + filter,
              deferred = $q.defer();


            solrService.delete(indicatorFilter, 'data').success(
              function () {
                // Remove monitoring
                solrService.delete(monitoringFilter, 'data').success(
                  function (data) {
                    deferred.resolve('Monitoring deleted.');
                  }
                ).error(function (response) {
                  deferred.reject(response);
                });
              }
            );

            return deferred.promise;
          }
        };
      }]);
}());