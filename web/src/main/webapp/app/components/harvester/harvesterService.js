(function () {
  "use strict";
  var app = angular.module('daobs');


  app.factory('harvesterService',
    ['$http', '$q', 'cfg', 'solrService',
      function ($http, $q, cfg, solrService) {
        return {
          getAll: function () {
            return $http.get(cfg.SERVICES.harvester + '.json');
          },
          run: function (h) {
            var deferred = $q.defer();
            $http.get(cfg.SERVICES.harvester + '/' + h.uuid + '?action=start').
              success(function (data) {
                deferred.resolve(data);
              });
            return deferred.promise;
          },
          add: function (h) {
            var deferred = $q.defer();
            $http.put(cfg.SERVICES.harvester, h).
              success(function (data) {
                deferred.resolve(data);
              });
            return deferred.promise;
          },
          remove: function (h) {
            var deferred = $q.defer();
            $http.delete(cfg.SERVICES.harvester + '/' + h.uuid).
              success(function (data) {
                deferred.resolve(data);
              });
            return deferred.promise;
          },
          removeRecords : function (h) {
            var filter = h === undefined ? '' :
                '+territory:"' + h.territory + '" ' +
                '+harvesterId:"' + h.url + '"',
              metadataFilter =
                '+(documentType:metadata documentType:association) ' + filter,
              deferred = $q.defer();


            solrService.delete(metadataFilter, 'data').success(
              function (data) {
                deferred.resolve('Records deleted.');
              }
            ).error(function (response) {
              deferred.reject(response);
            });

            return deferred.promise;
          }
        };
      }]);
}());