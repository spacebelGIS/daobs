(function () {
  "use strict";
  var app =  angular.module('daobs');

  app.config(['$routeProvider', '$translateProvider', 'cfg',
      function ($routeProvider, $translateProvider, cfg) {

      // Set translation provider to load JSON file
      $translateProvider.useStaticFilesLoader({
        prefix: 'assets/i18n/',
        suffix: '.json'
      });
      $translateProvider.preferredLanguage('en');

      $routeProvider
        .when('/', {
          templateUrl: cfg.SERVICES.root +
            '/app/components/home/homeView.html'
        }).when('/harvesting', {
          templateUrl: cfg.SERVICES.root +
            '/app/components/harvester/harvesterView.html'
        }).when('/logout', {
          controller: 'LogoutCtrl',
          templateUrl: cfg.SERVICES.root +
            '/app/components/home/homeView.html'
        }).otherwise({
          redirectTo: '/'
        });
    }]);
}());