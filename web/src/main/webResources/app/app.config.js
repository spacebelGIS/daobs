(function () {
  "use strict";
  var app =  angular.module('daobs');

  var context = '${webapp.rootUrl}';

  app.constant('cfg', {
    'SERVICES': {
      root: context,
      // TODO: dashboard core should be a parameter
      dashboardCoreName: 'dashboard',
      dataCoreName: 'data',
      dashboardCore: context + 'dashboard',
      dataCore: context + 'data',
      harvester: context + 'daobs/harvester',
      reportingConfig: context + 'daobs/reporting.json',
      reporting: context + 'daobs/reporting/',
      samples: context + 'daobs/samples/',
      reportingSubmit: context + 'data/update/xslt',
      dashboardBaseURL: context + 'dashboard2/#/dashboard/solr/',
      solrAdmin: context + 'admin.html'
    }
  });
}());