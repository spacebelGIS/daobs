(function () {
  "use strict";
  var app = angular.module('daobs');

  app.controller('HarvesterConfigCtrl', [
    '$scope', '$routeParams', '$http', 'cfg',
    function ($scope, $routeParams, $http, cfg) {
      $scope.harvesterConfig = null;
      $http.get(cfg.SERVICES.harvesterConfig).success(function (data) {
        $scope.harvesterConfig = data.harvester;
      });
    }]);
}());