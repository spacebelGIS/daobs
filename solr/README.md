## Install, configure and start Solr

### Manual installation

Download Solr from http://lucene.apache.org/solr/mirrors-solr-latest-redir.html
and copy it to the Solr module. eg. solr/solr-5.2.1

Download JTS from https://sourceforge.net/projects/jts-topo-suite/
and copy it to the Solr lib folder: server/solr-webapp/webapp/WEB-INF/lib

Download Saxon to add XSL version 2 support (see src/main/resources/lib).

Start Solr.

Create collection
```
cd solr/solr-5.2.1/bin
./solr create -p 8984 -c silk -d ../../src/main/solr-cores/silk/
./solr create -p 8984 -c data -d ../../src/main/solr-cores/data/
./solr create -p 8984 -c dashboard -d ../../src/main/solr-cores/dashboard/
```

### Maven installation

Maven could take care of the installation steps:
* download
* initialize collection
* start

Use the following commands:

```
cd solr
mvn install -Psolr-download
mvn install -Psolr-init
mvn exec:exec -Dsolr-start
```

