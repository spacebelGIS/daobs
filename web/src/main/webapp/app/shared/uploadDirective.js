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
  'use strict';

  var module = angular.module('daobs');
  module.directive('fileModel', ['$parse', function ($parse) {
    return {
      restrict: 'A',
      link: function (scope, element, attrs) {
        var model = $parse(attrs.fileModel),
          modelSetter = model.assign;

        element.bind('change', function () {
          scope.$apply(function () {
            modelSetter(scope, element[0].files);
          });
        });
      }
    };
  }]);

  module.directive('fileDropZone', function () {
    return {
      restrict: 'A',
      scope: {
        files: '=fileDropZone'
      },
      link: function (scope, element, attrs) {
        var isTypeValid, processDragOverOrEnter, validMimeTypes;
        processDragOverOrEnter = function (event) {
          if (event !== null) {
            event.preventDefault();
          }
          event.originalEvent.dataTransfer.effectAllowed = 'copy';
          return false;
        };

        element.bind('dragover', processDragOverOrEnter);
        element.bind('dragenter', processDragOverOrEnter);

        return element.bind('drop', function (event) {
          if (event !== null) {
            event.preventDefault();
          }
          var reader = new FileReader();
          scope.$apply(function () {
            scope.files = event.originalEvent.dataTransfer.files;
          });
          return false;
        });
      }
    };
  });
}());
