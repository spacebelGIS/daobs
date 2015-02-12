(function () {
  "use strict";
  var app = angular.module('daobs');

  app.controller('HarvesterConfigCtrl', [
    '$scope', '$routeParams', 'harvesterService', 'cfg',
    function ($scope, $routeParams, harvesterService, cfg) {
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
        harvesterService.remove(h).then(init);
      };
      $scope.removeRecords = function (h) {
        harvesterService.removeRecords(h).then(init);
      };

      init();
    }]);
}());