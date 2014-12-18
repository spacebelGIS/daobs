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
      root: '/solr',
      dashboardCore: '/solr/dashboard',
      dataCore: '/solr/data',
      harvesterConfig: '/solr/daobs/harvester.json',
      reportingConfig: '/solr/daobs/reporting.json',
      reporting: '/solr/daobs/reporting/',
      reportingSubmit: '/solr/data/update/xslt',
      dashboardBaseURL: '/solr/dashboard/#/dashboard/solr/',
      solrAdmin: '/solr/admin.html'
    }
  });

	  app.config(function($routeProvider) {
		$routeProvider
	      .when('/', {
	          controller: 'HomeCtrl',
	          templateUrl: 'lib/app/partials/home.html'
	        })
	      .when('/reporting', {
				controller : 'ReportingCtrl',
				templateUrl : 'lib/app/partials/reporting/generate.html'
			})
	      .when('/reporting/manage', {
				controller : 'ReportingCtrl',
				templateUrl : 'lib/app/partials/reporting/manage.html'
			})
	       .when('/reporting/submit', {
				controller : 'ReportingCtrl',
				templateUrl : 'lib/app/partials/reporting/submit.html'
			})
	       .when('/harvesting', {
	          controller: 'HarvestingCtrl',
	          templateUrl: 'lib/app/partials/harvesting.html'
	        })
	        .when('/logout', {
	          controller: 'LogoutCtrl',
	          templateUrl: 'lib/app/partials/home.html'
	        })
	        .otherwise({
	          redirectTo: '/'
	        });
	});
	  
  app.controller('RootController', [
    '$scope', '$location', '$http', 'cfg',
    function ($scope, $location, $http, cfg) {
      $scope.navLinks = [{
        id: 'home',
        text: 'Home',
        title: '',
        icon: 'glyphicon-home',
        url: '#/'
      }, {
        id: 'dashboard',
        text: 'Dashboard',
        title: 'View & create dashboards',
        icon: 'glyphicon-stats',
        url: 'dashboard'
      }, {
        id: 'report',
        text: 'Reporting',
        title: 'Create report',
        icon: 'glyphicon-list-alt',
        url: '#/reporting'
      }, {
        id: 'harvest',
        text: 'Harvesting',
        title: 'Harvest information',
        icon: 'glyphicon-download-alt',
        url: '#/harvesting'
      }, {
        id: 'admin',
        text: '',
        title: 'Admin console',
        icon: 'glyphicon-cog',
        url: 'admin.html'
      }, {
        id: 'logout',
        text: 'Logout',
        title: 'Logout',
        icon: 'glyphicon-user',
        url: '#/logout'
      }];

      // Change class based on route path
      $scope.currentRoute = null;
      $scope.navClass = function (page) {
        var path = $location.path().replace('/', '');
        $scope.currentRoute = path || 'home';
        return page.replace('#/', '') === $scope.currentRoute
        	|| $scope.currentRoute.indexOf(page.replace('#/', '') + "/") === 0 ? 'active' : '';
      };

      $scope.startIntro = function () {
        var intro = introJs();
        $http.get(cfg.SERVICES.root +
          '/lib/app/introConfig.json').
          success(function (data) {
            var items = data.steps.menu;
            items.push.apply(items, data.steps[$scope.currentRoute]);
            intro.setOptions({steps: items});
            intro.start();
          });
      }
    }]);

  app.controller('LogoutCtrl', ['$scope', '$http', 'cfg',
    function ($scope, $http, cfg) {
      // IE (1st check for IE11, 2on for previous versions)
      if ((Object.hasOwnProperty.call(window, "ActiveXObject") && !window.ActiveXObject) || (window.ActiveXObject)) {
        try {
          document.execCommand("ClearAuthenticationCache");
          window.location.href = cfg.SERVICES.root;
        } catch (exception) {}
      // Other browsers
      } else {
          var xmlhttp = new XMLHttpRequest();
          // page with logout message somewhere in not protected directory
          xmlhttp.open("GET", cfg.SERVICES.solrAdmin, true, "logout", (new Date()).getTime().toString());
          xmlhttp.send("");
          xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4) {
              window.location.href = cfg.SERVICES.root;
            }
          }
      }
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
      $scope.rules = null;
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

      function setReport(data) {
        $scope.report = data;
        $scope.rules = [];
        if (data.indicators.indicator) {
          $scope.rules.push.apply($scope.rules, data.indicators.indicator);
        }
        if (data.variables.variable) {
          $scope.rules.push.apply($scope.rules, data.variables.variable);
        }
      }

      // View report configuration
      $scope.getReportDetails = function () {
        $scope.territory = null;
        $http.get(cfg.SERVICES.reporting +
          $scope.reporting.id + '.json').success(function (data) {
          setReport(data);
          $scope.overview = true;
        });
      };

      // Preview report
      $scope.preview = function () {
        $scope.overview = false;
        $scope.report = null;
        var area = $scope.territory && $scope.territory.label,
          filterParameter = $scope.filter ? '?fq=' + $scope.filter  : '';
        $http.get(cfg.SERVICES.reporting +
          $scope.reporting.id + '/' +
          area + '.json' + filterParameter).success(function (data) {
          setReport(data);
        });
      };

      // Reset report on territory changes
      $scope.$watch('territory', function () {
        $scope.report = null;
      });
      
      $scope.isActive = function(hash) {
    	  return location.hash.indexOf("#/" + hash) === 0 
    	  	&& location.hash.indexOf("#/" + hash + "/") !== 0 ;
      }
    }]);
}());