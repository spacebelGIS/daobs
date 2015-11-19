
# Overview

Collect information and configure indicators to generate reporting. Build dashboards 
using the online creation tools. Share your dashboards and provide access to all 
monitoring information. This project is using [Solr](http://lucene.apache.org/solr/) and 
[Banana](https://github.com/LucidWorks/banana) dashboard tool to analyze geospatial metadata 
catalog content and [Tika analysis toolkit](https://tika.apache.org/) to analyze 
associated resources (eg. PDF, JSON, DBF).


* Advanced full-text search capabilities
* Create, share and visualize online dashboards
* Collect information using the OGC CSW standard
* Dataset indexing for better search
* Generate configurable reports


# User guide

The guide for user installing and configuring the application.

## Requirements

* Git
* Java 7
* Maven 3.1.0+
* Solr 5.x
* A modern web browser. The latest version of Chrome and Firefox have been tested to work. Safari also works, except for the "Export to File" feature for saving dashboards. We recommend that you use Chrome or Firefox while building dashboards. IE10+ should be also supported.

## Build the application

Clone and compile the application:

```
git clone --recursive https://github.com/titellus/daobs.git
cd daobs
mvn clean install
```

## Install and configure Solr

See [solr/README.md](solr/README.md).


## Run the application

2 options:

* Deploy the WAR file in a servlet container (eg. tomcat).
* Start the web application using maven.


### Using maven

```
cd web
mvn tomcat7:run-war
```

Access the home page from http://localhost:8983.


### Build a custom WAR file

In order to build a custom WAR file, update the following properties which are defined in the root pom.xml:
* war.name
* webapp.context
* webapp.url
* webapp.username
* webapp.password
* solr.core.data: Define the data core name (useful if more than one daobs instance use the same Solr)
* solr.core.dashboard: Define the dashboard core name


Run the following command line and copy the WAR which is built in web/target/{{war.name}}.war.
```
mvn clean install -Dwebapp.context=/dashboard \
                  -Dwebapp.rootUrl=/dashboard/ \
                  -Dwebapp.url=http://www.app.org \
                  -Dwebapp.username=admin \
                  -Dwebapp.password=secret
```




### Deploy a WAR file

Create a custom data directory.
```
mkdir /usr/dashboard/data
```

Unzip the WAR and check that the WEB-INF/config.properties point to this new directory.
Copy the defaults datadir from WEB-INF/datadir to the custom data directory:

```
# If using the source code
cp -fr web/target/solr/WEB-INF/datadir/* /usr/dashboard/data/.

# If using the WAR file
unzip dashboard.war
cp -fr WEB-INF/datadir/* /usr/dashboard/data/.
```


Copy the default core configuration folder to the solr.solr.home folder.

```
# If using the source code
mkdir /usr/dashboard/core
cp -fr web/target/solr-cores/* /usr/dashboard/core/.
```

Set the solr.solr.home system property which define the location of the Solr index. Example: For Tomcat set it in the catalina.sh file.

```
export JAVA_OPTS="$JAVA_OPTS -Dsolr.solr.home=/usr/dashboard/core"
```

Deploy the WAR file in Tomcat (or any Java container).

```
cp web/target/dashboard.war /usr/local/apache-tomcat/webapps/.
```

Run the container.

Access the home page from http://localhost:8080/dashboard.

If the Solr URL needs to be updated, look into the WEB-INF/config.properties file.



## Configuration

### Configure security

Administration pages are accessible only to non anonymous users.

By default, only one user is defined with username "admin" and password "admin". To add more user, configuration is made in WEB-INF/config-security-ba.xml.

### Other build options

#### Building the application in debug mode

For developers, the application could be built in debug mode in order to have the banana project installed without Javascript minification. For this disable the production profile:

```
mvn clean install -P\!production
```

#### Building the application without test

The tests rely on some third party application (eg. INSPIRE validator). It may be useful to build the application without testing:

```
mvn clean install -DskipTests
```


## Search engine architecture


2 Solr cores are created:
* one for storing dashboards
* one for storing metadata records and indicators


## Importing data
 
2 types of information can be loaded into the system:

* Metadata records following the standard for metadata on geographic information ISO19139/119
* Indicators in [INSPIRE monitoring reporting format](http://inspire-geoportal.ec.europa.eu/monitoringreporting/monitoring.xsd)


### Harvesting records

#### Harvester configuration

An harvester engine provides the capability to harvest metadata records from discovery service (CSW end-point).
The list of nodes to harvest is configured in harvester/csw-harvester/src/main/resources/WEB-INF/harvester/config-harvester.xml.


The configuration parameters are:
* territory: A representative geographic area for the node
* folder: The folder name where harvested records are stored
* name: The name of the node
* url: The server URL to request (should provide GetCapabilities and GetRecords operations)
* filter: (Optional) A OGC filter to restrict the search to a subset of the catalog


Example:

```
<harvester>
  <territory>de</territory>
  <folder>de</folder>
  <name>GeoDatenKatalog.De</name>
  <url>http://ims.geoportal.de/inspire/srv/eng/csw</url>
  <filter>
    <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
      <ogc:PropertyIsLike escapeChar="\" singleChar="?" wildCard="*">
        <ogc:PropertyName>AnyText</ogc:PropertyName>
        <ogc:Literal>inspireidentifiziert</ogc:Literal>
      </ogc:PropertyIsLike>
    </ogc:Filter>
  </filter>
</harvester>
```

#### Running harvester

Harvesting records from a CSW end-point:

```
cd harvesters/csw-harvester
mvn camel:run
```

#### Harvester details

[Apache Camel](http://camel.apache.org/) integration framework based on Enterprise Integration Patterns is used to easily create configurable harvesters by defining routing and mediation rules.

The CSW harvester strategy is the following:
* GetRecords to retrieve the number of metadata to be harvested
* Compute paging information
* GetRecords for each pages and index the results in Solr.


While harvesting the GetRecords query and response are saved on disk. A log file return detailed information about the current process.


Harvesting is multithreaded on endpoint basis. By default, configuration is 11 threads across harvesters which means no multithreaded requests on the same server but 11 nodes could be harvested in parallel. 



### Indexing ISO19139 records

Metadata records and indicators could be manually loaded using Solr API by importing XML files.
First you need to apply the following XSL transformation to convert ISO metadata
to Solr XML transaction document.

After the transformation, you can then load the output documents using:

```
# Load files in all subfolders
find . -name *.xml -type f |
while read f
do
  echo "importing '$f' file..";
  curl "http://localhost:8983/data/update?commit=true" \
    -u admin:admin \
    -H "Content-Type: text/xml; charset=utf-8" \
    --data-binary @$f
done
```


### Indexing INSPIRE indicators


Manually indexing INSPIRE monitoring:

```
for f in *.xml; do
  echo "importing '$f' file..";
  curl "http://localhost:8983/data/update?commit=true&tr=inspire-monitoring-reporting.xsl" -u admin:admin -H "Content-Type: text/xml; charset=utf-8" --data-binary @$f
done
```

To import monitoring with ancillary information:
```
for f in *.xml; do
  echo "importing '$f' file..";
  curl "http://localhost:8983/data/update?commit=true&tr=inspire-monitoring-reporting-with-ai.xsl" -u admin:admin -H "Content-Type: text/xml; charset=utf-8" --data-binary @$f
done
```




## Removing documents


Manually dropped all records:
```
curl http://localhost:8983/data/update \
    --data '<delete><query>documentType:*</query></delete>' \
    -u admin:admin \
    -H 'Content-type:text/xml; charset=utf-8'

curl http://localhost:8983/data/update \
    --data '<commit/>' \
    -u admin:admin \
    -H 'Content-type:text/xml; charset=utf-8'
```

The search query could be adapted to restrict to a subset of documents:
* reportingYear:2014 for removing reporting for 2014





## Analysis tasks

A set of background tasks could be triggered on the content of the index and 
improve or add information to the index.

### Validation task

#### Process

A two steps validation task is defined:

* XML Schema validation
* INSPIRE validator (http://inspire-geoportal.ec.europa.eu/validator2/#)

The results and details of the validation process are stored in the index:

* For INSPIRE validation:
 * isValid: Boolean
 * validDate: Date of validation
 * validReport: XML report returned by the validation service
 * validInfo: Text information about the status
 * completenessIndicator: Completeness indicator reported by the validation tool
 * isAboveThreshold: Boolean. Set to true if the completeness indicator is above a value defined in the validation task configuration
* For XML Schema validation:
 * isSchemaValid: Boolean
 * schemaValidDate: The date of validation
 * schemaValidReport: XSD validation report


#### Run the task

To trigger the validation:

```
cd tasks/validation-checker
mvn camel:run
```

By default, the task validates all records which have not been validated before (ie. +documentType:metadata -isValid:[* TO *]). A custom set of records could be validated by changing the solr.select.filter in the config.properties file.


### Services and data sets link

#### Process

A data sets may be accessible through a view and/or download services. This type 
of relation is defined at the service metadata level using the operatesOn element:

* link using the data sets metadata record UUID:

```
<srv:operatesOn uuidref="81aea739-4d21-427d-bec4-082cb64b825b"/>
```

* link using a GetRecordById request:
```
<srv:operatesOn uuidref="BDML_NATURES_FOND"
                xlink:href="http://services.data.shom.fr/csw/ISOAP?service=CSW&version=2.0.2&request=GetRecordById&Id=81aea739-4d21-427d-bec4-082cb64b825b"/>
```

Both type of links are supported. The GetRecordById takes priority. The data sets 
metadata record identifier is extracted from the GetRecordById request.



This task analyze all available services in the index and update associated data 
sets by adding the following fields:

* recordOperatedByType: Contains the type of all services operating the data sets (eg. view, download)
* recordOperatedBy: Contains the identifier of all services operating the data sets. Note: it does not provide information that this service is a download service. User need to get the service record to get this details.

The task also propagate INSPIRE theme from each datasets to the service.


#### Run the task

To trigger the validation:

```
cd tasks/service-dataset-indexer
mvn camel:run
```

By default, the task analyze all services.




### Associated resource indexer

#### Process

A metadata record may contain URL to remote resources (eg. PDF document, ZIP files).
This task will retrieve the content of such document using [Tika analysis toolkit](https://tika.apache.org/)
and index the content retrieved. This improve search results has the data related
to the metadata are also indexed.


Associated document URL are stored in the linkUrl field in the index.


#### Run the task

To trigger the data analysis:

```
cd tasks/data-indexer
mvn camel:run
```



## Dashboard


Access the dashboard page, click load and choose dashboard configuration from the list. 
If no dashboards are available sample dashboard are available here: dashboard/src/app/dashboards

* Browse: Search for metadata records and filter your search easily (facets, INSPIRE themes and annexes charts).
* INSPIRE-Dashboard: Home page
* default: Monitoring reporting 2013 dashboard

By default no dashboard are loaded.

User can load a set of dashboards using the /daobs/samples/dashboard service.

Eg. http://localhost:8983/daobs/samples/dashboard/INSPIRE.json will load all INSPIRE specific dashboards.

2 sets of dashboards are available:
* INSPIRE* about INSPIRE monitoring
* CATALOG* for dashboards on harvested records.


## Reporting

Report configuration is made web/src/main/webapp/WEB-INF/reporting.
One or more configuration file can be created in this folder. The file name should follow the pattern "config-{{report_id}}.xml".

A report is created from a set of variables and indicators. Variables are defined using query expressions to be computed by the search engine. Indicators are created from mathematical expressions based on variables.


TODO: Add more doc on how to configure indicators.




