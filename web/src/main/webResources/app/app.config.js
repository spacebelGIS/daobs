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

  var context = '${webapp.rootUrl}';
  var solrContext = '/${solr.webapp.name}';

  app.constant('cfg', {
    'SERVICES': {
      root: context,
      solrRoot: solrContext,
      // TODO: dashboard core should be a parameter
      dashboardCoreName: '${solr.core.dashboard}',
      dashboardCore: solrContext + '/${solr.core.dashboard}',
      dataCoreName: '${solr.core.data}',
      dataCore: solrContext + '/${solr.core.data}',
      reportingSubmit: solrContext + '/${solr.core.data}/update',
      harvester: context + 'daobs/harvester',
      workersStats: context + 'daobs/workers',
      reportingConfig: context + 'daobs/reporting.json',
      reporting: context + 'daobs/reporting/',
      samples: context + 'daobs/samples/',
      dashboardBaseURL: context + 'dashboard2/#/dashboard/solr/',
      solrAdmin: solrContext + '/admin.html',
      eftValidation: context + 'daobs/etf-validator'
    }
  });
}());
