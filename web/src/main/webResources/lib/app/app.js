(function () {
  "use strict";
  /*jslint todo: true */
  /**
   *
   * TODO: Add translate module
   * @type {module|*}
   */
  var app = angular.module('daobs', ['ngRoute']);
  var context = '${webapp.context}' === '/' ?
      '${webapp.context}' : '${webapp.context}/';
  app.constant('cfg', {
    'SERVICES': {
      root: context,
      dashboardCore: context + 'dashboard',
      dataCore: context + 'data',
      harvesterConfig: context + 'daobs/harvester.json',
      reportingConfig: context + 'daobs/reporting.json',
      reporting: context + 'daobs/reporting/',
      samples: context + 'daobs/samples/',
      reportingSubmit: context + 'data/update/xslt',
      dashboardBaseURL: context + 'dashboard/#/dashboard/solr/',
      solrAdmin: context + 'admin.html'
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
   * Controller for home page displaying dashboards. Used also on reporting.
   * available.
   */
  app.controller('HomeCtrl', ['$scope', '$http', 'cfg',
    function ($scope, $http, cfg) {
      $scope.dashboards = null;
      $scope.dashboardsLoaded = null;
      $scope.listOfDashboardToLoad = null;

      var init = function () {
        $scope.dashboardBaseURL = cfg.SERVICES.dashboardBaseURL;
        $http.get(cfg.SERVICES.dashboardCore +
          '/select?q=title:*&wt=json&rows=20&sort=title asc').
          success(function (data) {
            $scope.dashboards = data.response.docs;
          });

        $http.get(cfg.SERVICES.samples + '/dashboardType.json').
          success(function (data) {
            $scope.listOfDashboardToLoad = data;
          });
      };

      $scope.loadDashboard = function (type) {
        $scope.dashboardsLoaded = null;
        return $http.put(cfg.SERVICES.samples +
        '/dashboard/' + type + '*.json').
          success(function (data) {
            $scope.dashboardsLoaded = data;
            init();
          });
      };

      init();
    }]);

  /**
   * Controller for home page search indicators
   */
  app.controller('SearchIndicatorCtrl', ['$scope', '$http', 'cfg',
    function ($scope, $http, cfg) {
      $scope.indicators = null;
      $scope.listOfIndicatorTerritory = [];
      $scope.listOfReportingYear = [];
      $scope.indicatorTerritory = null;
      $scope.reportingYear = null;	        		
      $scope.dropSubmitMessage = '';
      $scope.dropSubmitStyle= '';

      var search = function () {

          var territory = null;
          if($scope.indicatorTerritory && $scope.indicatorTerritory.value) {
        	  territory = $scope.indicatorTerritory.value;
          }
          var year = null;
          if($scope.reportingYear && $scope.reportingYear.value) {
        	  year = $scope.reportingYear.value;
          }
          if(!territory || !year){
        	  //Only search if we selected both fields
        	  return;
          }
        $http.get(cfg.SERVICES.dataCore +
          '/select?q=+documentType:indicator AND reportingYear:' + year + 
          ' AND territory:' + territory
          + '&wt=json&sort=indicatorName asc').
          success(function (data) {
            $scope.indicators = data.response.docs;
          });
      };
      
      $scope.updateTerritory = function () {
          $scope.listOfIndicatorTerritory = [];  
	      $http.get(cfg.SERVICES.dataCore +
	    	        '/select?q=' +
	    	        'documentType:indicator&' +
	    	        'start=0&rows=0&wt=json&indent=true&' +
	    	        'facet=true&facet.field=territory').success(function (data) {
	    	        var i = 0, facet = data.facet_counts.facet_fields.territory;
	    	        // The facet response contains an array
	    	        // with [value1, countFor1, value2, countFor2, ...]
	    	        do {
	    	          // If it has records
	    	          if (facet[i + 1] > 0) {
	    	            $scope.listOfIndicatorTerritory.push({
	    	              label: facet[i].toLowerCase(),
	    	              value: facet[i]
	    	            });
	    	          }
	    	          i = i + 2;
	    	        } while (i < facet.length);
	    	      });
      }
      
      $scope.updateYears = function() {
          $scope.listOfReportingYear = [];
          var territory = "*";
          if($scope.indicatorTerritory && $scope.indicatorTerritory.value) {
        	  territory = $scope.indicatorTerritory.value;
          }
	      $http.get(cfg.SERVICES.dataCore +
	    	        '/select?q=' +
	    	        'documentType:indicator AND territory:' + territory   + '&' +
	    	        'start=0&rows=0&wt=json&indent=true&' +
	    	        'facet=true&facet.field=reportingYear').success(function (data) {
	    	        var i = 0, facet = data.facet_counts.facet_fields.reportingYear;
	    	        // The facet response contains an array
	    	        // with [value1, countFor1, value2, countFor2, ...]
	    	        do {
	    	          // If it has records
	    	          if (facet[i + 1] > 0) {
	    	            $scope.listOfReportingYear.push({
	    	              label: facet[i],
	    	              value: facet[i],
	    	              selected: facet[i] == $scope.selectedYear
	    	            });
	    	          }
	    	          i = i + 2;
	    	        } while (i < facet.length);
	    	      });
      }


      $scope.drop = function() {
    	  var territory = null;
          if($scope.indicatorTerritory && $scope.indicatorTerritory.value) {
        	  territory = $scope.indicatorTerritory.value;
          }
          var year = null;
          if($scope.reportingYear && $scope.reportingYear.value) {
        	  year = $scope.reportingYear.value;
          }
          if(!territory || !year){
        	  //Only drop if we selected both fields
        	  return;
          }
          angular.element("#mr-btn-report-drop").disabled = true;
          
          var del = {};
          del["delete"] = {};
          var d = del["delete"];
          d.query ='documentType:indicator AND reportingYear:' 
  	        + year + ' AND territory:' + territory;
          
	      $http.post(cfg.SERVICES.dataCore + '/update', del)
	        .success(function (data) {
	        	$http.post(cfg.SERVICES.dataCore + '/update', {commit: {}}).success(function (data) {
	        		$scope.dropSubmitMessage = 'Reports dropped.';
                    $scope.dropSubmitStyle= 'success';
                    angular.element("#mr-btn-report-drop").disabled = false;
                    $scope.updateTerritory();
                    $scope.updateYears();
                    $scope.indicators = null;
        	      })
                  .error(function(e){
                      $scope.dropSubmitMessage = 'Sorry, the drop failed. Contact with the administrator.';
                      $scope.dropSubmitStyle= 'error';
                      angular.element("#mr-btn-report-drop").disabled = false;
                  });
	      })
          .error(function(e){
              $scope.dropSubmitMessage = 'Sorry, the drop failed. Contact with the administrator.';
              $scope.dropSubmitStyle= 'error';
              angular.element("#mr-btn-report-drop").disabled = false;
          });
      }
         
      $scope.$watch('indicatorTerritory', search);
      $scope.$watch('indicatorTerritory', $scope.updateYears);
      $scope.$watch('reportingYear', search);
      
      $scope.updateTerritory();
      $scope.updateYears();
      
    }]);


  /**
   * Controller retrieving harvesting configuration.
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
          filterParameter = $scope.filter ? '?fq=' + $scope.filter  : '?';
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