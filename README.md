
# Overview

Collect information and configure indicators to generate reporting. Build dashboards using the online creation tools. Share your dashboards and provide access to all monitoring information. This project is using [Solr](http://lucene.apache.org/solr/) and [Banana](https://github.com/LucidWorks/banana) dashboard tool to analyze geospatial metadata catalog content.


* Advanced full-text search capabilities
* Create, share and visualize online dashboards
* Collect information using the OGC CSW standard
* Generate configurable reports


# User guide

The guide for user installing and configuring the application.

## Requirements

* Git
* Java 7
* Maven 3

## Installing the application

Clone and compile the application:

```
git clone --recursive https://github.com/titellus/daobs.git
cd daobs
mvn clean install
```


Starting web application:

```
cd web
mvn tomcat7:run-war
```

Access the home page from http://localhost:8983/solr.


## Search engine architecture


2 Solr cores are created:
* one for storing dashboards
* one for storing metadata records and indicators


## Importing metadata records or indicators
 
2 types of information can be loaded into the system:

* Metadata records following the standard for metadata on geographic information ISO19139/119
* Indicators in [INSPIRE monitoring reporting format](http://inspire-geoportal.ec.europa.eu/monitoringreporting/monitoring.xsd)



### Harvesting records

#### Harvester configuration

An harvester engine provides the capability to harvest metadata records from discovery service (CSW end-point). The list of nodes to harvest is configured in src/main/resources/config-harvester.xml.


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
cd harvesters
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



### Indexing records and indicators

Metadata records and indicators could be manually loaded using Solr API by importing XML files.


Manually index XML records:

```
# Load file in current directory
for f in *.xml; do
  echo "importing '$f' file..";
  curl "http://localhost:8983/solr/data/update/xslt?commit=true&tr=metadata-iso19139.xsl" \
     -H "Content-Type: text/xml; charset=utf-8" \
     --data-binary @$f
done


# Load files in all subfolders
find . -name *.xml -type f |
while read f
do
  echo "importing '$f' file..";
  curl "http://localhost:8983/solr/data/update/xslt?commit=true&tr=metadata-iso19139.xsl" -H "Content-Type: text/xml; charset=utf-8" --data-binary @$f
done

or 

for f in *.xml; do
  echo "importing '$f' file..";
  curl "http://localhost:8983/solr/data/update/xslt?commit=true&tr=metadata-iso19139.xsl" -H "Content-Type: text/xml; charset=utf-8" --data-binary @$f
done
```

Manually indexing INSPIRE monitoring reporting:

```
for f in *.xml; do
  echo "importing '$f' file..";
  curl "http://localhost:8983/solr/data/update/xslt?commit=true&tr=inspire-monitoring-reporting.xsl" -H "Content-Type: text/xml; charset=utf-8" --data-binary @$f
done
```

Manually dropped all records:
```
curl http://localhost:8983/solr/data/update \
    --data '<delete><query>documentType:*</query></delete>' \
    -H 'Content-type:text/xml; charset=utf-8'

curl http://localhost:8983/solr/data/update \
    --data '<commit/>' \
    -H 'Content-type:text/xml; charset=utf-8'
```


### Indexing ISO19139 records




## Loading dashboards

Access the dashboard page, click load and choose dashboard configuration from the list. 
If no dashboards are available sample dashboard are available here: dashboard/src/app/dashboards

* Browse: Search for metadata records and filter your search easily (facets, INSPIRE themes and annexes charts).
* INSPIRE-Dashboard: Home page
* default: Monitoring reporting 2013 dashboard




## Configuring reports

Report configuration is made web/src/main/webapp/WEB-INF/reporting.
One or more configuration file can be created in this folder. The file name should follow the pattern "config-<report_id>.xml".

A report is created from a set of variables and indicators. Variables are defined using query expressions to be computed by the search engine. Indicators are created from mathematical expressions based on variables.






