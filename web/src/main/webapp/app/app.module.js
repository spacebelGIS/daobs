(function () {
  "use strict";
  /*jslint todo: true */
  /**
   *
   * @type {module|*}
   */
  var app = angular.module('daobs', [
    'ngRoute',
    'pascalprecht.translate',
    'solr']);

  app.controller('RootController', [
    '$scope', '$location', '$http', 'cfg',
    function ($scope, $location, $http, cfg) {
      $scope.navLinks = [{
        id: 'home',
        icon: 'glyphicon-home',
        url: '#/'
      }, {
        id: 'dashboard',
        icon: 'glyphicon-stats',
        url: 'dashboard'
      }, {
        id: 'monitoring',
        icon: 'glyphicon-list-alt',
        url: '#/monitoring/manage'
      }, {
        id: 'harvesting',
        icon: 'glyphicon-download-alt',
        url: '#/harvesting'
      }, {
        id: 'admin',
        icon: 'glyphicon-cog',
        url: 'admin.html'
      }, {
        id: 'signout',
        icon: 'glyphicon-user',
        url: '#/logout'
      }];

      // Change class based on route path
      $scope.currentRoute = null;
      $scope.navClass = function (page) {
        var path = $location.path().replace('/', '');
        $scope.currentRoute = path || 'home';
        return page.replace('#/', '') === $scope.currentRoute ||
          $scope.currentRoute.indexOf(page.replace('#/', '') + "/") === 0 ? 'active' : '';
      };

      $scope.startIntro = function () {
        var intro = introJs();
        $http.get(cfg.SERVICES.root +
          'assets/introConfig.json').
          success(function (data) {
            var items = data.steps.menu;
            items.push.apply(items, data.steps[$scope.currentRoute]);
            intro.setOptions({steps: items});
            intro.start();
          });
      };
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
}());