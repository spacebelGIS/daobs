(function () {
  "use strict";
  var app =  angular.module('daobs');

  app.config(['$routeProvider', '$translateProvider',
      function ($routeProvider, $translateProvider) {

      // Set translation provider to load JSON file
      $translateProvider.useStaticFilesLoader({
        prefix: 'assets/i18n/',
        suffix: '.json'
      });
      $translateProvider.preferredLanguage('en');

      $routeProvider
        .when('/', {
          templateUrl: 'app/components/home/homeView.html'
        }).when('/harvesting', {
          templateUrl: 'app/components/harvester/harvesterView.html'
        }).when('/logout', {
          controller: 'LogoutCtrl',
          templateUrl: 'app/components/home/homeView.html'
        }).otherwise({
          redirectTo: '/'
        });
    }]);
}());