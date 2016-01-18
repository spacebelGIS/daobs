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
      $scope.hasINSPIREdashboard = false;
      $scope.hasOnlyINSPIREdashboard = true;
      $scope.solrConnectionError = null;

      var init = function () {
        $scope.dashboardBaseURL = cfg.SERVICES.dashboardBaseURL;
        $http.get(cfg.SERVICES.dashboardCore +
        '/select?q=title:*&wt=json&sort=title asc&start=0&rows=80&fl=id,title').
          then(function (response) {
            $scope.dashboards = response.data.response.docs;
            angular.forEach($scope.dashboards, function (d) {
              if ($scope.startsWith(d, 'inspire')) {
                $scope.hasINSPIREdashboard = true;
              } else {
                $scope.hasOnlyINSPIREdashboard = false;
              }
            });
          }, function (response) {
          if (response.status = 500) {
            $scope.solrConnectionError = response;
          }
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
        return solrService.delete(documentFilter, 'dashboard').then(
          init
        );
      };

      $scope.startsWith = function (actual, expected) {
        if (angular.isObject(actual)) {
          actual = actual.title;
        }
        var lowerStr = (actual + "").toLowerCase();
        return lowerStr.indexOf(expected.toLowerCase()) === 0;
      };
      $scope.notStartsWith = function (actual, expected) {
        return !$scope.startsWith(actual, expected);
      };
      init();
    }]);
}());