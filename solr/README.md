Download Solr from http://lucene.apache.org/solr/mirrors-solr-latest-redir.html
and copy it to the Solr module. eg. solr/solr-5.2.1

Download JTS from https://sourceforge.net/projects/jts-topo-suite/
and copy it to the Solr lib folder: server/solr-webapp/webapp/WEB-INF/lib

To start Solr use:

```
mvn exec:exec -Dsolr-start
```