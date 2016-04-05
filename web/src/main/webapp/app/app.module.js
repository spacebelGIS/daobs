/*
 * Copyright 2014-2016 European Environment Agency
 *
 * Licensed under the EUPL, Version 1.1 or – as soon
 * they will be approved by the European Commission -
 * subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
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
    'solr',
    'csw',
    'ui-notification']);

  app.controller('RootController', [
    '$scope', '$location', '$http', 'cfg',
    function ($scope, $location, $http, cfg) {
      $scope.navLinks = [{
        id: 'home',
        icon: 'fa-home',
        url: '#/'
      }, {
        id: 'dashboard',
        icon: 'fa-stats',
        // TODO: Should be displayed only if dashboard available
        // TODO: Should point to a dashboard that exist
        url: 'dashboard2/#/dashboard/solr/INSPIRE%20Indicator%20trends'
      }, {
        id: 'monitoring',
        icon: 'fa-list-alt',
        url: '#/monitoring/manage'
      }, {
        id: 'harvesting',
        icon: 'fa-cloud-download',
        url: '#/harvesting/manage'
      }, {
        id: 'admin',
        icon: 'fa-cog',
        url: '/solr'
        //TODO cfg.SERVICES.solrRoot + '/' but Solr admin will not be rewritten
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
          'assets/introConfig.json').success(function (data) {
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
      if ((Object.hasOwnProperty.call(window, "ActiveXObject") && !window.ActiveXObject) || (window.ActiveXObject)) {
        try {
          document.execCommand("ClearAuthenticationCache");
          window.location.href = cfg.SERVICES.root;
        } catch (exception) {
        }
        // Other browsers
      } else {
        var xmlhttp = new XMLHttpRequest();
        // page with logout message somewhere in not protected directory
        xmlhttp.open("GET", cfg.SERVICES.solrAdmin, true, "logout", (new Date()).getTime().toString());
        xmlhttp.send("");
        xmlhttp.onreadystatechange = function () {
          if (xmlhttp.readyState == 4) {
            window.location.href = cfg.SERVICES.root;
          }
        }
      }
    }]);
}());
