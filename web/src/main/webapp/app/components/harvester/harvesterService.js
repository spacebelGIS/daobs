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


  app.factory('harvesterService',
    ['$http', '$q', 'cfg', 'solrService',
      function ($http, $q, cfg, solrService) {
        return {
          getAll: function () {
            return $http.get(cfg.SERVICES.harvester + '.json');
          },
          run: function (h) {
            var deferred = $q.defer();
            $http.get(cfg.SERVICES.harvester + '/' + h.uuid + '/jms').success(function (data) {
              deferred.resolve(data);
            }).error(function (response) {
              deferred.reject(response);
            });
            return deferred.promise;
          },
          add: function (h) {
            var deferred = $q.defer();
            $http.put(cfg.SERVICES.harvester, h).success(function (data) {
              deferred.resolve(data);
            }).error(function (response) {
              deferred.reject(response);
            });
            return deferred.promise;
          },
          remove: function (h) {
            var deferred = $q.defer();
            $http.delete(cfg.SERVICES.harvester + '/' + h.uuid).success(function (data) {
              deferred.resolve(data);
            }).error(function (response) {
              deferred.reject(response);
            });
            return deferred.promise;
          },
          removeRecords: function (h) {
            var filter = h === undefined ? '' :
              '+territory:"' + h.territory.trim() + '" ' +
              '+harvesterUuid:"' + h.uuid.trim() + '"',
              metadataFilter =
                '+(documentType:metadata documentType:association) ' + filter,
              deferred = $q.defer();


            solrService.delete(metadataFilter, 'data').then(
              function (data) {
                deferred.resolve('Records deleted.');
              }, function (response) {
                deferred.reject(response);
              });

            return deferred.promise;
          },
          eftValidation: function (h, all) {
            var query = '+harvesterUuid:' + h.uuid + ' +documentType:metadata +resourceType:service';
            if (!all) {
              query = query + ' -etfValidDate:[* TO *]';
            }

            var deferred = $q.defer();
            $http.get(cfg.SERVICES.eftValidation, {params: {fq: query}}).success(function (data) {
              deferred.resolve(data);
            }).error(function (response) {
              deferred.reject(response);
            });
            return deferred.promise;
          }
        };
      }]);
}());
