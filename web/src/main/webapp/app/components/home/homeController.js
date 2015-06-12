(function () {
  "use strict";
  var app = angular.module('daobs');

  /**
   * Controller for home page displaying dashboards. Used also on reporting.
   * available.
   */
  app.controller('HomeCtrl', ['$scope', '$http', 'cfg', 'solrService',
    function ($scope, $http, cfg, solrService) {
      $scope.dashboards = null;
      $scope.dashboardsLoaded = null;
      $scope.listOfDashboardToLoad = null;

      var init = function () {
        $scope.dashboardBaseURL = cfg.SERVICES.dashboardBaseURL;
        $http.get(cfg.SERVICES.dashboardCore +
        '/select?q=title:*&wt=json&sort=title asc&start=0&rows=40').
          success(function (data) {
            $scope.dashboards = data.response.docs;
          });

        $http.get(cfg.SERVICES.samples + 'dashboardType.json').
          success(function (data) {
            $scope.listOfDashboardToLoad = data;
          });
      };

      // TODO: Move to dashboard service
      $scope.loadDashboard = function (type) {
        $scope.dashboardsLoaded = null;
        return $http.put(cfg.SERVICES.samples +
        '/dashboard/' + type + '*.json').
          success(function (data) {
            $scope.dashboardsLoaded = data;
            init();
          });
      };

      $scope.removeDashboard = function (id) {
        var documentFilter = id ? 'id:"' + id + '"' : '*:*';
        return solrService.delete(documentFilter, 'dashboard').success(
          init
        );
      };
      init();
    }]);
}());