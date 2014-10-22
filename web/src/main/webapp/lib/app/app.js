(function () {
  "use strict";
  /*jslint todo: true */
  /**
   *
   * TODO: Add translate module
   * @type {module|*}
   */
  var app = angular.module('daobs', ['ngRoute']);

  app.constant('cfg', {
    'SERVICES': {
      dashboardCore: '/solr/dashboard',
      dataCore: '/solr/data',
      harvesterConfig: '/solr/daobs/harvester.json',
      reportingConfig: '/solr/daobs/reporting.json',
      reporting: '/solr/daobs/reporting/',
      dashboardBaseURL: '/solr/dashboard/#/dashboard/solr/'
    }
  });

  app.config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        controller: 'HomeCtrl',
        templateUrl: 'lib/app/partials/home.html'
      })
      .when('/reporting', {
        controller: 'ReportingCtrl',
        templateUrl: 'lib/app/partials/reporting.html'
      })
      .when('/harvesting', {
        controller: 'HarvestingCtrl',
        templateUrl: 'lib/app/partials/harvesting.html'
      })
      .otherwise({
        redirectTo: '/'
      });
  });


  app.controller('RootController', [
    '$scope', '$location',
    function ($scope, $location) {
      $scope.navLinks = [{
        text: 'Home',
        title: '',
        icon: 'glyphicon-home',
        url: '#/'
      }, {
        text: 'Dashboard',
        title: 'View & create dashboards',
        icon: 'glyphicon-stats',
        url: 'dashboard'
      }, {
        text: 'Reporting',
        title: 'Create report',
        icon: 'glyphicon-list-alt',
        url: '#/reporting'
      }, {
        text: 'Harvesting',
        title: 'Harvest information',
        icon: 'glyphicon-download-alt',
        url: '#/harvesting'
      }];

      // Change class based on route path
      $scope.navClass = function (page) {
        var currentRoute = $location.path() || 'home';
        return page.replace('#', '') === currentRoute ? 'active' : '';
      };
    }]);


  /**
   * Controller for home page displaying dashboards
   * available.
   */
  app.controller('HomeCtrl', ['$scope', '$http', 'cfg',
    function ($scope, $http, cfg) {
      $scope.dashboards = null;

      var init = function () {
        $scope.dashboardBaseURL = cfg.SERVICES.dashboardBaseURL;
        $http.get(cfg.SERVICES.dashboardCore +
          '/select?q=title:*&wt=json&rows=20&sort=title asc').
          success(function (data) {
            $scope.dashboards = data.response.docs;
          });
      };

      init();
    }]);


  /**
   * Controller retrieveing harvesting configuration.
   *
   * TODO:
   * * configuration of harvester
   * * view ongoing harvesting tasks
   */
  app.controller('HarvestingCtrl', ['$scope', '$http', 'cfg',
    function ($scope, $http, cfg) {
      $scope.harvesterConfig = null;
      $http.get(cfg.SERVICES.harvesterConfig).success(function (data) {
        $scope.harvesterConfig = data.harvester;
      });
    }]);


  /**
   * Controller displaying reports configuration
   * and generating/exporting report.
   *
   * TODO:
   * * submit report
   */
  app.controller('ReportingCtrl', ['$scope', '$http', 'cfg',
    function ($scope, $http, cfg) {
      $scope.listOfTerritory = [];
      $scope.territory = null;
      $scope.reporting = null;
      $scope.report = null;
      $scope.overview = false;

      $scope.reportingConfig = null;
      $http.get(cfg.SERVICES.reportingConfig).success(function (data) {
        $scope.reportingConfig = data.reporting;
        $scope.reporting = $scope.reportingConfig[0];
      });
      // Get list of territory available
      $http.get(cfg.SERVICES.dataCore +
        '/select?q=' +
        'documentType%3Ametadata&' +
        'start=0&rows=0&' +
        'wt=json&indent=true&' +
        'facet=true&facet.field=territory').success(function (data) {
        var i = 0, facet = data.facet_counts.facet_fields.territory;
        // The facet response contains an array
        // with [value1, countFor1, value2, countFor2, ...]
        do {
          // If it has records
          if (facet[i + 1] > 0) {
            $scope.listOfTerritory.push({
              label: facet[i].toLowerCase(),
              count: facet[i + 1]
            });
          }
          i = i + 2;
        } while (i < facet.length);
      });

      // View report configuration
      $scope.overview = function () {
        $scope.territory = null;
        $http.get(cfg.SERVICES.reporting +
          $scope.reporting.id + '.json').success(function (data) {
          $scope.report = data;
          $scope.overview = true;
        });
      };

      // Preview report
      $scope.preview = function () {
        $scope.overview = false;
        $scope.report = null;
        var area = $scope.territory && $scope.territory.label;
        $http.get(cfg.SERVICES.reporting +
          $scope.reporting.id + '/' +
          area + '.json').success(function (data) {
          $scope.report = data;
        });
      };

      // Reset report on territory changes
      $scope.$watch('territory', function () {
        $scope.report = null;
      });
    }]);
}());