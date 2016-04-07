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

  app.config(['$routeProvider', 'cfg',
    function ($routeProvider, cfg) {
      $routeProvider.when('/harvesting/:section', {
        templateUrl: cfg.SERVICES.root +
        'app/components/harvester/harvesterView.html'
      }).when('/harvesting/harvesting', {
        templateUrl: cfg.SERVICES.root +
        'app/components/harvester/harvesterView.html'
      });
    }]);

  app.controller('HarvesterCtrl', ['$scope', '$routeParams',
    function ($scope, $routeParams) {
      $scope.section = $routeParams.section || 'harvester';

      $scope.isActive = function (hash) {
        return location.hash.indexOf("#/" + hash) === 0
          && location.hash.indexOf("#/" + hash + "/") !== 0;
      }
    }]);

  app.controller('HarvesterConfigCtrl', [
    '$scope', '$routeParams', '$translate', '$timeout', '$http', '$location',
    'harvesterService', 'cfg', 'Notification', 'cswService',
    function ($scope, $routeParams, $translate, $timeout, $http, $location,
              harvesterService, cfg, Notification, cswService) {
      $scope.harvesterConfig = null;
      $scope.pollingInterval = '10s';
      $scope.adding = false;
      $scope.harvesterTpl = {
        territory: null,
        name: null,
        url: null,
        filter: null,
        folder: null,
        pointOfTruthURLPattern: null,
        serviceMetadata: null,
        nbOfRecordsPerPage: null,
        uuid: null
      };
      $scope.newHarvester = $scope.harvesterTpl;

      $scope.translations = null;
      $translate(['errorRemovingHarvester',
        'errorRemovingHarvesterRecords',
        'harvesterStarted',
        'harvesterDeleted',
        'harvesterSaved',
        'errorGettingRemoteHits',
        'errorAddingHarvester',
        'harvesterRecordsDeleted',
        'errorStartingHarvester',
        'eftValidationStarted',
        'errorStartingEftValidation']).then(function (translations) {
        $scope.translations = translations;
      });

      $scope.statsForTerritory = {};
      $scope.statsForRemote = {};

      function loadStatsForTerritory() {
        if ($location.path().indexOf('/harvesting/manage') === 0) {
          var statsField = ['isValid', 'etfIsValid'], statsFieldConfig = [];
          for (var i = 0; i < statsField.length; i++) {
            statsFieldConfig.push(statsField[i] + ": { type : terms, " +
              "field: " + statsField[i] + ", missing: true }");
          }
          $http.get(
            cfg.SERVICES.dataCore + '/select?' +
            $.param({
              'q': '+documentType:metadata',
              'rows': '0',
              'wt': 'json',
              'json.facet': "{'top_territory': { " +
              "type: terms, field: harvesterUuid, " +
              "limit: " + $scope.harvesterConfig.length +
              ", missing: true," +
              "facet: {" + statsFieldConfig.join(',') +
              "}}}"
            })
          ).then(function (data) {
            if (data.data.facets.top_territory && data.data.facets.top_territory.buckets) {
              var facets = data.data.facets.top_territory.buckets;
              for (var i = 0; i < facets.length; i++) {
                $scope.statsForTerritory[facets[i].val] = facets[i];
              }
            }
          });
        }
        $timeout(function () {
          loadStatsForTerritory()
        }, 5000);
      };

      $scope.loading = false;
      function init() {
        $scope.loading = true;
        harvesterService.getAll().success(function (list) {
          $scope.loading = false;
          $scope.harvesterConfig = list.harvester;
          if (list.harvester.length > 0) {
            loadStatsForTerritory()
          }
        });
      }
      $scope.orderByFields = ['name', 'territory', 'url', 'count', 'remoteError'];
      $scope.predicate = 'territory';
      $scope.order = function (predicate, reverse) {
        var isNewPredicate = $scope.predicate === predicate;

        if (predicate === 'count') {
          $scope.predicate = function (h) {
            return $scope.statsForTerritory[h.uuid] &&
                    $scope.statsForTerritory[h.uuid].count;
          };
        } else if (predicate === 'remoteError') {
          $scope.predicate = function (h) {
            return $scope.statsForRemote[h.uuid] &&
                    $scope.statsForRemote[h.uuid].error;
          };
        } else {
          $scope.predicate = predicate;
        }
        $scope.reverse = reverse ? reverse : (
          isNewPredicate ? !$scope.reverse : false);
      }

      $scope.getHitsNumber = function (h) {
        if (h) {
          getHitsNumber(h);
        } else {
          angular.forEach($scope.harvesterConfig, function (h) {
            getHitsNumber(h);
          });
        }
      };
      function getHitsNumber (h) {
        $scope.statsForRemote[h.uuid] = {};
        cswService.getHitsNumber(h.url, h.filter).then(function (nbHits) {
          $scope.statsForRemote[h.uuid] = {
            count: nbHits,
            when: new Date()
          };
        }, function (response) {
          Notification.error(
            $scope.translations.errorGettingRemoteHits + ' ' +
            (response.error || response.data) +
            ' (' + response.status + ').');
          $scope.statsForRemote[h.uuid] = {
            error: (response.error || response.data) +
                   '(' + response.status + ')',
            when: new Date()
          };
        });
      };

      $scope.edit = function (h) {
        $scope.adding = true;
        $scope.newHarvester = h;
        $('body').scrollTop(0);
      }

      $scope.add = function () {
        harvesterService.add($scope.newHarvester).then(function (response) {
          $scope.adding = false;
          Notification.success($scope.translations.harvesterSaved);
          init();
          $scope.newHarvester = $scope.harvesterTpl;
        }, function (response) {
          Notification.error(
            $scope.translations.errorAddingHarvester + ' ' +
            response.message);
        });
      };

      $scope.run = function (h) {
        harvesterService.run(h).then(function () {
          Notification.success(
            $translate($scope.translations.harvesterStarted,
                       {name: h.name}));
        }, function (response) {
          Notification.error(
            $scope.translations.errorStartingHarvester + ' ' +
            response);
        });
      };
      $scope.runAll = function () {
        angular.forEach($scope.harvesterConfig, function (h) {
          $scope.run(h);
        });
      };

      $scope.remove = function (h) {
        harvesterService.remove(h).then(function (response) {
          Notification.success($scope.translations.harvesterDeleted);
          init();
        }, function (response) {
          Notification.error(
            $scope.translations.errorRemovingHarvester + ' ' +
            response.error.msg);
        });
      };
      $scope.removeRecords = function (h) {
        harvesterService.removeRecords(h).then(function (response) {
          Notification.success($scope.translations.harvesterRecordsDeleted);
          init();
        }, function (response) {
          console.error(response);
          Notification.error(
            $scope.translations.errorRemovingHarvesterRecords + ' ' +
            response.error.msg);
        })
      };

      $scope.eftValidation = function (h, all) {
        harvesterService.eftValidation(h, all).then(function (response) {
          Notification.success($scope.translations.eftValidationStarted);
          init();
        }, function (response) {
          Notification.error(
            $scope.translations.errorStartingEftValidation + ' ' +
            response.error.msg);
        });
      };

      init();
    }]);


  app.controller('WorkersMonitorCtrl', [
    '$scope', '$timeout', '$http', '$location', 'cfg',
    function ($scope, $timeout, $http, $location, cfg) {

      function load() {
        if ($location.path().indexOf('/harvesting/monitor') === 0) {
          $http.get(
            cfg.SERVICES.workersStats
          ).then(function (response) {
            $scope.workersStats = response.data;
          });
        }
        $timeout(function () {
          load()
        }, 5000);
      };

      load()
    }]);
}());
