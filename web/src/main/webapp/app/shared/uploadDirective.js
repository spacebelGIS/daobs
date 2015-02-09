(function () {
  'use strict';

  var module = angular.module('daobs');
  module.directive('fileModel', ['$parse', function ($parse) {
    return {
      restrict: 'A',
      link: function(scope, element, attrs) {
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
        processDragOverOrEnter = function(event) {
          if (event !== null) {
            event.preventDefault();
          }
          event.originalEvent.dataTransfer.effectAllowed = 'copy';
          return false;
        };

        element.bind('dragover', processDragOverOrEnter);
        element.bind('dragenter', processDragOverOrEnter);

        return element.bind('drop', function(event) {
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