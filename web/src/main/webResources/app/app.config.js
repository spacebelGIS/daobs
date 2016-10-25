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
  var app = angular.module('daobs_cfg', []);

  var context = '${webapp.rootUrl}';
  var api = '${webapp.rootUrl}api/';
  var solrContext = '/${solr.webapp.name}';

  app.constant('cfg', {
    'repository': '${repository}',
    'version': '${buildNumber}',
    'defaultDashboard': '${dashboard.default}',
    'SERVICES': {
      root: context,
      solrRoot: solrContext,
      solrAdmin: '${solr.admin.url}',
      dashboardCoreName: '${solr.core.dashboard}',
      dashboardCore: api + 'search/${solr.core.dashboard}',
      esdashboardCore: context + 'es/.dashboards',
      dataCoreName: '${solr.core.data}',
      dataCore: api + 'search/${solr.core.data}',
      esdataCore: context + 'es/records',
      esindicatorCore: context + 'es/indicators',
      proxy: context + 'proxy?url=',
      harvester: api + 'harvester',
      harvesters: api + 'harvesters',
      workersStats: api + 'activities',
      dashboards: api + 'dashboards',
      reportingConfig: api + 'reports.json',
      reports: api + 'reports',
      samples: api + 'samples',
      dashboardBaseURL: context + 'dashboard/app/kibana?#/dashboard/',
      eftValidation: api + 'validate/etf'
    }
  });
}());
