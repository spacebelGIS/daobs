
This project is using Solr (http://lucene.apache.org/solr/) and Banana dashboard tool 
to analyze metadata catalog content.



# Overview

* Advanced full-text search capabilities
* Online dashboard creation tool
* CSW harvesting
* Reports generator



# User guide

The guide for user installing and configuring the application.


## Installing the application

Compile the application:

```
mvn clean install
```


Starting web application:

```
cd web
mvn tomcat7:run-war
```

Access the Solr admin page from http://localhost:8983/solr.
Access the dashboard from http://localhost:8983/solr/dashboard.


## Search engine architecture


2 Solr cores are created:
* one for dashboards
* one for metadata records and indicators



## Importing metadata records or indicators
 
2 types of information can be loaded into the system:

* Metadata records in ISO19139 format
* Indicators in INSPIRE monitoring reporting format (http://inspire-geoportal.ec.europa.eu/monitoringreporting/monitoring.xsd)



### Harvesting records

#### Harvester configuration

An harvester engine provides the capability to harvest metadata records from discovery service (CSW end-point).
The list of nodes to harvest is configured in src/main/resources/config-harvester.xml.

The configuration parameters are:
* territory: A representative geographic area for the node
* folder: The folder name where harvested record are stored
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

Apache Camel (http://camel.apache.org/) integration framework based on Enterprise Integration Patterns is used to easily create configurable harvesters by defining routing and mediation rules.

The CSW harvester strategy is the following:
* GetRecords to retrieve the number of metadata to be harvested
* Compute paging
* GetRecords for each page and index them in Solr.

While harvesting the GetRecords query and response are saved on disk. A log file return detailed information about the current process.

Harvesting could be multithreaded. By default, configuration is 11 threads across harvesters which means no multithread requests on the same server. 



### Indexing records and indicators

Metadata records and indicators could be manually loaded using Solr API.


Manually index XML records:

```
for f in *.xml; do
  echo "importing '$f' file..";
  curl "http://localhost:8983/solr/data/update/xslt?commit=true&tr=metadata-iso19139.xsl" \
     -H "Content-Type: text/xml; charset=utf-8" \
     --data-binary @$f
done
```

Manually indexing INSPIRE monitoring reporting:

```
for f in *.xml; do
  echo "importing '$f' file..";
  curl "http://localhost:8983/solr/data/update/xslt?commit=true&tr=inspire-monitoring-reporting.xsl" -H "Content-Type: text/xml; charset=utf-8" --data-binary @$f
done
```

Manually dropped records:
```
curl http://localhost:8983/solr/data/update \
    --data '<delete><query>documentType:*</query></delete>' \
    -H 'Content-type:text/xml; charset=utf-8'

curl http://localhost:8983/solr/data/update \
    --data '<commit/>' \
    -H 'Content-type:text/xml; charset=utf-8'
```

## Loading dashboards

Access the dashboard page, click load and choose dashboard configuration from
https://github.com/titellus/banana/tree/develop/src/app/dashboards

* Browse: Search for metadata records and filter your search easily (facets, INSPIRE themes and annexes charts).
* INSPIRE-Dashboard: Home page
* default: Monitoring reporting 2013 dashboard




## Computing indicators



## Indexing associated resources



