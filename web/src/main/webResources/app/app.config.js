(function () {
  "use strict";
  var app =  angular.module('daobs');

  var context = '${webapp.rootUrl}';
  var solrContext = '${solr.webapp.name}';

  app.constant('cfg', {
    'SERVICES': {
      root: context,
      // TODO: dashboard core should be a parameter
      dashboardCoreName: '${solr.core.dashboard}',
      dashboardCore: solrContext + '/${solr.core.dashboard}',
      dataCoreName: '${solr.core.data}',
      dataCore: solrContext + '/${solr.core.data}',
      reportingSubmit: solrContext + '/${solr.core.data}/update',
      harvester: context + 'daobs/harvester',
      reportingConfig: context + 'daobs/reporting.json',
      reporting: context + 'daobs/reporting/',
      samples: context + 'daobs/samples/',
      dashboardBaseURL: context + 'dashboard2/#/dashboard/solr/',
      solrAdmin: solrContext + '/admin.html'
    }
  });
}());