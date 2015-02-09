(function () {
  "use strict";
  var app =  angular.module('daobs');

  var context = '${webapp.context}' === '/' ?
    '${webapp.context}' : '${webapp.context}/';

  app.constant('cfg', {
    'SERVICES': {
      root: context,
      dashboardCore: context + 'dashboard',
      dataCore: context + 'data',
      harvesterConfig: context + 'daobs/harvester.json',
      reportingConfig: context + 'daobs/reporting.json',
      reporting: context + 'daobs/reporting/',
      samples: context + 'daobs/samples/',
      reportingSubmit: context + 'data/update/xslt',
      dashboardBaseURL: context + 'dashboard/#/dashboard/solr/',
      solrAdmin: context + 'admin.html'
    }
  });
}());