(function () {
  "use strict";
  var app = angular.module('daobs');

  app.config(['$routeProvider',
    function ($routeProvider) {
      $routeProvider.when('/monitoring/manage', {
          controller : 'MonitoringCtrl',
          templateUrl : 'app/components/monitoring/reporting/manage.html'
        }).when('/monitoring/submit', {
          controller : 'MonitoringCtrl',
          templateUrl : 'app/components/monitoring/reporting/submit.html'
        });
    }]);


  /**
   * Controller displaying reports configuration
   * and generating/exporting report.
   *
   * TODO:
   * * submit report
   */
  app.controller('MonitoringCtrl', ['$scope', '$http', '$routeParams',
    'cfg',
    function ($scope, $http, $routeParams, cfg) {
      $scope.listOfTerritory = [];
      $scope.territory = null;
      $scope.reporting = null;
      $scope.report = null;
      $scope.rules = null;
      $scope.overview = false;
      $scope.reportingConfig = null;
      $scope.section = $routeParams.section;

      // Get the list of monitoring types
      $http.get(cfg.SERVICES.reportingConfig, {cache: true}).
        success(function (data) {
        $scope.reportingConfig = data.reporting;
        $scope.reporting = $scope.reportingConfig[0];
      });


      // Get list of territory available
      $http.get(cfg.SERVICES.dataCore +
      '/select?q=' +
      'documentType%3Ametadata&' +
      'start=0&rows=0&' +
      'wt=json&indent=true&' +
      'facet=true&facet.field=territory', {cache: true}).
        success(function (data) {
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

      $scope.filterOnTerritory = function (t) {
        $scope.territory = t;
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


  /**
   * Controller for general information about
   * current monitoring.
   */
  app.controller('MonitoringInfoCtrl', [
    '$scope', '$http', 'cfg', 'solrService', 'monitoringService',
    function ($scope, $http, cfg, solrService, monitoringService) {
      $scope.listOfMonitoring = null;
      $scope.monitoringFacet = null;

      //$scope.listOfIndicatorTerritory = [];
      //$scope.listOfReportingYear = [];
      //$scope.indicatorTerritory = null;
      //$scope.reportingYear = null;
      //$scope.dropSubmitMessage = '';
      //$scope.dropSubmitStyle= '';

      var init = function () {
        monitoringService.loadMonitoring().then(function (response) {
          $scope.listOfMonitoring = response.monitoring;
          $scope.monitoringFacet = response.facet;
        });
      };

      $scope.removeMonitoring = function (m) {
        // TODO: Handle oops
        monitoringService.removeMonitoring(m).then(
          function() {
            init();
          });
      };


      //
      //var search = function () {
      //
      //  var territory = null;
      //  if($scope.indicatorTerritory && $scope.indicatorTerritory.value) {
      //    territory = $scope.indicatorTerritory.value;
      //  }
      //  var year = null;
      //  if($scope.reportingYear && $scope.reportingYear.value) {
      //    year = $scope.reportingYear.value;
      //  }
      //  if(!territory || !year){
      //    //Only search if we selected both fields
      //    return;
      //  }
      //
      //};
      //
      //$scope.updateTerritory = function () {
      //  $scope.listOfIndicatorTerritory = [];
      //  $http.get(cfg.SERVICES.dataCore +
      //  '/select?q=' +
      //  'documentType:indicator&' +
      //  'start=0&rows=0&wt=json&indent=true&' +
      //  'facet=true&facet.field=territory').success(function (data) {
      //    var i = 0, facet = data.facet_counts.facet_fields.territory;
      //    // The facet response contains an array
      //    // with [value1, countFor1, value2, countFor2, ...]
      //    do {
      //      // If it has records
      //      if (facet[i + 1] > 0) {
      //        $scope.listOfIndicatorTerritory.push({
      //          label: facet[i].toLowerCase(),
      //          value: facet[i]
      //        });
      //      }
      //      i = i + 2;
      //    } while (i < facet.length);
      //  });
      //}
      //
      //$scope.updateYears = function() {
      //  $scope.listOfReportingYear = [];
      //  var territory = "*";
      //  if($scope.indicatorTerritory && $scope.indicatorTerritory.value) {
      //    territory = $scope.indicatorTerritory.value;
      //  }
      //  $http.get(cfg.SERVICES.dataCore +
      //  '/select?q=' +
      //  'documentType:indicator AND territory:' + territory   + '&' +
      //  'start=0&rows=0&wt=json&indent=true&' +
      //  'facet=true&facet.field=reportingYear').success(function (data) {
      //    var i = 0, facet = data.facet_counts.facet_fields.reportingYear;
      //    // The facet response contains an array
      //    // with [value1, countFor1, value2, countFor2, ...]
      //    do {
      //      // If it has records
      //      if (facet[i + 1] > 0) {
      //        $scope.listOfReportingYear.push({
      //          label: facet[i],
      //          value: facet[i],
      //          selected: facet[i] == $scope.selectedYear
      //        });
      //      }
      //      i = i + 2;
      //    } while (i < facet.length);
      //  });
      //}
      //
      //
      //$scope.drop = function() {
      //  var territory = null;
      //  if($scope.indicatorTerritory && $scope.indicatorTerritory.value) {
      //    territory = $scope.indicatorTerritory.value;
      //  }
      //  var year = null;
      //  if($scope.reportingYear && $scope.reportingYear.value) {
      //    year = $scope.reportingYear.value;
      //  }
      //  if(!territory || !year){
      //    //Only drop if we selected both fields
      //    return;
      //  }
      //  angular.element("#mr-btn-report-drop").disabled = true;
      //
      //  var del = {};
      //  del["delete"] = {};
      //  var d = del["delete"];
      //  d.query ='documentType:indicator AND reportingYear:'
      //  + year + ' AND territory:' + territory;
      //
      //  $http.post(cfg.SERVICES.dataCore + '/update', del)
      //    .success(function (data) {
      //      $http.post(cfg.SERVICES.dataCore + '/update', {commit: {}}).success(function (data) {
      //        $scope.dropSubmitMessage = 'Reports dropped.';
      //        $scope.dropSubmitStyle= 'success';
      //        angular.element("#mr-btn-report-drop").disabled = false;
      //        $scope.updateTerritory();
      //        $scope.updateYears();
      //        $scope.indicators = null;
      //      })
      //        .error(function(e){
      //          $scope.dropSubmitMessage = 'Sorry, the drop failed. Contact with the administrator.';
      //          $scope.dropSubmitStyle= 'error';
      //          angular.element("#mr-btn-report-drop").disabled = false;
      //        });
      //    })
      //    .error(function(e){
      //      $scope.dropSubmitMessage = 'Sorry, the drop failed. Contact with the administrator.';
      //      $scope.dropSubmitStyle= 'error';
      //      angular.element("#mr-btn-report-drop").disabled = false;
      //    });
      //}
      //
      //$scope.$watch('indicatorTerritory', search);
      //$scope.$watch('indicatorTerritory', $scope.updateYears);
      //$scope.$watch('reportingYear', search);
      //
      //$scope.updateTerritory();
      //$scope.updateYears();
      init();

    }]);
}());