(function () {
  "use strict";
  var app = angular.module('daobs');

  app.controller('HarvesterConfigCtrl', [
    '$scope', '$routeParams', 'harvesterService', 'cfg',
    function ($scope, $routeParams, harvesterService, cfg) {
      $scope.harvesterConfig = null;
      $scope.pollingInterval = '10s';

      function init() {
        harvesterService.getAll().success(function (list) {
          $scope.harvesterConfig = list.harvester;
        });
      }

      $scope.add = function () {
        var h = {
          uuid: 'test',
          territory: 'titellus',
          name: 'titellus',
          url: 'http://titellus.net'
        };
        harvesterService.add(h);
      };

      $scope.run = function (h) {
        harvesterService.run(h);
      };

      $scope.remove = function (h) {
        harvesterService.remove(h).then(init);
      };

      init();
    }]);
}());