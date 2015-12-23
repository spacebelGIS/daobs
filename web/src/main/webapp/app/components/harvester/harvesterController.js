(function () {
  "use strict";
  var app = angular.module('daobs');

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
        $http.get(cfg.SERVICES.dataCore +
          '/select?q=' +
          'documentType%3Ametadata&' +
          'start=0&rows=0&wt=json&' +
          'facet=true&facet.sort=index&facet.field=territory').
        success(function (data) {
          $scope.facetValues = {};
          var i = 0, facet = data.facet_counts.facet_fields.territory;
          do {
            if (facet[i + 1] > 0) {
              $scope.statsForTerritory[facet[i]] = facet[i + 1];
            }
            i = i + 2;
          } while (i < facet.length);
        });

        $timeout(function () {
          loadStatsForTerritory()
        }, 5000);
      };

      function init() {
        harvesterService.getAll().success(function (list) {
          $scope.harvesterConfig = list.harvester;
        });
        loadStatsForTerritory()
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

      init();
    }]);
}());