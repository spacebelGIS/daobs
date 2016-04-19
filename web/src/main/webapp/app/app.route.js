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
  var app = angular.module('daobs');

  app.config(['$routeProvider', '$translateProvider', 'cfg',
    function ($routeProvider, $translateProvider, cfg) {
      // Set translation provider to load JSON file
      $translateProvider.useStaticFilesLoader({
          prefix: 'assets/i18n/',
          suffix: '.json'
        })
        .registerAvailableLanguageKeys(['en', 'fr'], {
          'fr': 'fr',
          '*': 'en'
        })
        .determinePreferredLanguage();

      $routeProvider
        .when('/', {
          templateUrl: cfg.SERVICES.root +
          'app/components/home/homeView.html'
        }).when('/logout', {
        controller: 'LogoutCtrl',
        templateUrl: cfg.SERVICES.root +
        'app/components/home/homeView.html'
      }).when('/login', {
        controller:'LoginController',
        templateUrl: cfg.SERVICES.root +
        'app/components/login/loginTemplate.html'
      }).otherwise({
        redirectTo: '/'
      });
    }]);
}());
