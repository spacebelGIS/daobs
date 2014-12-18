(function () {
  'use strict';

  var module = angular.module('daobs');

  module.directive('reportUpload', function($http, cfg){
    return {
      restrict: 'A',
      link: function(scope) {
        function file_selected(evt) {
        	 scope.reportingSubmitMessage = '';
             scope.reportingSubmitStyle = '';
          angular.element
          ("#reportUpload").disabled = true;
          var files = evt.target.files; // FileList object
          var readerOnload = function() {
            return function(e) {               
               $http.post(cfg.SERVICES.reportingSubmit + "?commit=true&tr=inspire-monitoring-reporting.xsl", e.target.result, {
                   transformRequest: angular.identity
               })
               .success(function(e){
                   scope.reportingSubmitMessage = 'Report added successfully';
                   scope.reportingSubmitStyle= 'success';
                   angular.element("#reportUpload").disabled = false;
                   
               })
               .error(function(e){
                   scope.reportingSubmitMessage = 'Sorry, the upload failed. Contact with the administrator. Error is: '
                	    + e.substring(e.indexOf("<str name=\"msg\">") + 16, e.indexOf("</str>"));
                   scope.reportingSubmitStyle= 'error';
                   angular.element("#reportUpload").disabled = false;
               });
              scope.$apply();
            };
          };
          for (var i = 0, f; f = files[i]; i++) {
            var reader = new FileReader();
            reader.onload = (readerOnload)(f);
            reader.readAsArrayBuffer(f);
            reader.fileName = f.name;
          }
        }
        // Check for the various File API support.
        if (window.File && window.FileReader && window.FileList && window.Blob) {
          // Something
          document.getElementById('reportUpload')
          		.addEventListener('change', file_selected, false);
        } else {
          scope.reportingSubmitMessage = 'Sorry, the HTML5 File APIs are not fully supported in this browser.';
          scope.reportingSubmitStyle= 'error';
        }
      }
    };
  });
}());