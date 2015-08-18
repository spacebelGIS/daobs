(function () {
  "use strict";
  var app =  angular.module('daobs');

  var context = '${webapp.rootUrl}';

  app.constant('cfg', {
    'SERVICES': {
      root: context,
      // TODO: dashboard core should be a parameter
      dashboardCoreName: '${solr.core.dashboard}',
      dataCoreName: '${solr.core.data}',
      dashboardCore: context + '${solr.core.dashboard}',
      dataCore: context + '${solr.core.data}',
      harvester: context + 'daobs/harvester',
      reportingConfig: context + 'daobs/reporting.json',
      reporting: context + 'daobs/reporting/',
      samples: context + 'daobs/samples/',
      reportingSubmit: context + '${solr.core.data}/update/xslt',
      dashboardBaseURL: context + 'dashboard2/#/dashboard/solr/',
      solrAdmin: context + 'admin.html'
    }
  });
}());