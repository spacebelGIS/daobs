(function () {
  "use strict";
  var app =  angular.module('daobs');

  app.filter('fromNow', [function () {
      return function (date) {
        return moment(date).fromNow();
      }
    }]);
}());