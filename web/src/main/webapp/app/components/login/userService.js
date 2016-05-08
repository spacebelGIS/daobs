/* Copyright 2014-2016 European Environment Agency
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
  var app = angular.module('user_service', ['daobs_cfg']);

  app.factory('userService', ['$http', '$q', 'cfg', '$httpParamSerializerJQLike',
    function($http, $q, cfg, $httpParamSerializerJQLike) {
      var currentUser = null


      return {
        login: function (username, password) {
          var deferred = $q.defer();
          $http.post(cfg.SERVICES.root + 'login',
            $httpParamSerializerJQLike({
              username: username,
              password: password
            }),
            {
              headers: {'Content-Type': 'application/x-www-form-urlencoded'},

            }
          ).success(function(data, status, headers) {
            currentUser = data;
            deferred.resolve(data);
          }).error(function(response, status, headers) {
            currentUser = null;
            deferred.reject(response);
          });
          return deferred.promise;

        },
        getCurrentUserInfo: function() {
          var deferred = $q.defer();
          $http.get(cfg.SERVICES.root + "api/me").success(function (data) {
            currentUser = data;
            deferred.resolve(data);
          }).error(function(response) {
            currentUser = null;
            deferred.reject(response);
          });

          return deferred.promise;
        },
        getUser: function() {
          return angular.copy(currentUser);
        }

      };
    }
  ]);


})();
