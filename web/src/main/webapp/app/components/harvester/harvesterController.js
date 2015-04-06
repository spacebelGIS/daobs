(function () {
  "use strict";
  var app = angular.module('daobs');

  app.controller('HarvesterConfigCtrl', [
    '$scope', '$routeParams', '$translate',
    'harvesterService', 'cfg',
    function ($scope, $routeParams, $translate,
              harvesterService, cfg) {
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
      $translate(['errorRemovingHarvester', 'errorRemovingHarvesterRecords']).
        then(function (translations) {
          $scope.translations = translations;
        });

      function init() {
        harvesterService.getAll().success(function (list) {
          $scope.harvesterConfig = list.harvester;
        });
      }

      $scope.edit = function (h) {
        $scope.adding = true;
        $scope.newHarvester = h;
        // TODO: Scroll top
      }

      $scope.add = function () {
        harvesterService.add($scope.newHarvester).then(function (response) {
          $scope.adding = false;
          init();
          $scope.newHarvester = $scope.harvesterTpl;
        });
      };

      $scope.run = function (h) {
        harvesterService.run(h);
      };

      $scope.remove = function (h) {
        harvesterService.remove(h).then(init, function (response) {
          alert($scope.translations.errorRemovingHarvester + ' ' +
            response.error.msg);
        });
      };
      $scope.removeRecords = function (h) {
        harvesterService.removeRecords(h).then(init, function (response) {
          alert($scope.translations.errorRemovingHarvesterRecords + ' ' +
          response.error.msg);
        })
      };

      init();
    }]);
}());