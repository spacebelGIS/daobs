(function () {
  "use strict";
  var app = angular.module('daobs');

  app.config(['$routeProvider',
    function ($routeProvider) {
      $routeProvider.when('/monitoring', {
        templateUrl : 'app/components/monitoring/monitoringView.html'
      }).when('/monitoring/:section', {
        templateUrl : 'app/components/monitoring/monitoringView.html'
      }).when('/monitoring/manage', {
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
  app.controller('MonitoringCtrl', ['$scope', '$routeParams',
    function ($scope, $routeParams) {
      $scope.section = $routeParams.section;

      $scope.isActive = function (hash) {
        return location.hash.indexOf("#/" + hash) === 0
          && location.hash.indexOf("#/" + hash + "/") !== 0;
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
      $scope.monitoringFilter = {};

      var init = function () {
        monitoringService.loadMonitoring().then(function (response) {
          $scope.listOfMonitoring = response.monitoring;
          $scope.monitoringFacet = response.facet;
        });
      };

      $scope.setMonitoringFilter = function (field, value) {
        $scope.monitoringFilter[field] = value;
      };

      $scope.removeMonitoring = function (m) {
        // TODO: Handle oops
        monitoringService.removeMonitoring(m).then(
          function() {
            init();
          });
      };

      $scope.setAsOfficialMonitoring = function (m) {
        alert('not supported yet');
      }

      init();
    }]);

  /**
   * Controller for general information about
   * current monitoring.
   */
  app.controller('MonitoringCreateCtrl', [
    '$scope', '$http', 'cfg', 'solrService', 'monitoringService',
    function ($scope, $http, cfg, solrService, monitoringService) {
      $scope.report = null;
      $scope.rules = null;
      $scope.overview = false;
      $scope.reportingConfig = null;

      function init() {
        //Get list of territory available
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
      };

      // Get the list of monitoring types
      $http.get(cfg.SERVICES.reportingConfig, {cache: true}).
        success(function (data) {
          $scope.reportingConfig = data.reporting;
          $scope.reporting = $scope.reportingConfig[0];
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

      init();
    }]);


  /**
   * Controller for submit new monitoring.
   */
  app.controller('MonitoringSubmitCtrl', [
    '$scope', '$http', '$translate', 'cfg', 'monitoringService',
    function ($scope, $http, $translate, cfg, monitoringService) {
      $scope.isOfficial = false;
      $scope.withRowData = false;
      $scope.isSubmitting = false;
      $scope.monitoringFiles = [];
      $scope.responseMessages = [];

      var listOfDeffered;
      function addMessage (text, name) {
        $translate(text, {
          filename: name
        }).then(function (translation) {
          $scope.responseMessages.push({
            label: translation,
            success: true
          });
          $scope.isSubmitting =
            $scope.responseMessages.length !== listOfDeffered.length;
        });
      };

      $scope.uploadMonitoring = function(){
        if ($scope.monitoringFiles.length === 0) {
          return;
        }

        $scope.isSubmitting = true;
        $scope.responseMessages = [];
        listOfDeffered = [];

        listOfDeffered = monitoringService.uploadMonitoring(
          $scope.monitoringFiles,
          $scope.isOfficial,
          $scope.withRowData
        );

        angular.forEach(listOfDeffered, function (item) {
          item.promise.then(function(data){
            addMessage('monitoringSubmitSuccess', item.file.name);
          }, function(response){
            addMessage('monitoringSubmitError', item.file.name);
          });
        });
      }
  }]);
}());