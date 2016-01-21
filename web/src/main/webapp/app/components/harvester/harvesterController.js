(function () {
  "use strict";
  var app = angular.module('daobs');

  app.config(['$routeProvider', 'cfg',
    function ($routeProvider, cfg) {
      $routeProvider.when('/harvesting/:section', {
        templateUrl : cfg.SERVICES.root +
        'app/components/harvester/harvesterView.html'
      }).when('/harvesting/harvesting', {
        templateUrl : cfg.SERVICES.root +
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
    '$scope', '$routeParams', '$translate', '$timeout', '$http',
    'harvesterService', 'cfg', 'Notification',
    function ($scope, $routeParams, $translate, $timeout, $http,
              harvesterService, cfg, Notification) {
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
        'errorAddingHarvester',
        'harvesterRecordsDeleted',
        'errorStartingHarvester']).
        then(function (translations) {
          $scope.translations = translations;
        });

      $scope.statsForTerritory = {};

      function loadStatsForTerritory() {
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
            'json.facet':
              "{'top_territory': { " +
                  "type: terms, field: territory, missing: true," +
                  "facet: {" + statsFieldConfig.join(',') +
              "}}}"
          })
        ).then(function (data) {
          if (angular.isDefined(data.data.facets.top_territory.buckets)) {
            var facets = data.data.facets.top_territory.buckets;
            for (var i = 0; i < facets.length; i++) {
              $scope.statsForTerritory[facets[i].val] = facets[i];
            }
          }
        });

        $timeout(function () {
          loadStatsForTerritory()
        }, 5000);
      };

      function init() {
        harvesterService.getAll().success(function (list) {
          $scope.harvesterConfig = list.harvester;
          if (list.harvester.length > 0) {
            loadStatsForTerritory()
          }
        });
      }

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
        }, function(response) {
          console.error(response);
          Notification.error(
            $scope.translations.errorAddingHarvester + ' ' +
            response.message);
        });
      };

      $scope.run = function (h) {
        harvesterService.run(h).then(function() {
          Notification.success($scope.translations.harvesterStarted);
        }, function(response) {
          console.error(response);
          Notification.error(
            $scope.translations.errorStartingHarvester + ' ' +
            response);
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
    '$scope', '$timeout', '$http', 'cfg',
    function ($scope, $timeout, $http, cfg) {

      function load() {
        $http.get(
          cfg.SERVICES.workersStats
        ).then(function (response) {
          $scope.workersStats = response.data;
        });

        $timeout(function () {
          load()
        }, 5000);
      };

      load()
    }]);
}());