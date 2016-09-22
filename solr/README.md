## Install, configure and start Solr

### Manual installation

Download Solr from http://lucene.apache.org/solr/mirrors-solr-latest-redir.html
and copy it to the Solr module. eg. solr/solr-6.0.1

Download JTS from https://sourceforge.net/projects/jts-topo-suite/
and copy it to the Solr lib folder: server/solr-webapp/webapp/WEB-INF/lib

Download Saxon to add XSL version 2 support (see src/main/resources/lib).

Start Solr.

Create collection
```
cd solr/solr-config/solr-5.3.1/bin
./solr start -p 8984 -c
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
cd solr/solr-config
mvn install -Psolr-download
mvn install -Psolr-init
mvn exec:exec -Dsolr-start
```

### Production use

Configure Solr to start on server startup.

Use solr/solr-6.0.1/bin/init.d/solr. Modify the script to start
Solr in cloud mode:

```
su -c "SOLR_INCLUDE=$SOLR_ENV $SOLR_INSTALL_DIR/bin/solr $SOLR_CMD -c" - $RUNAS
```


### Updating a collection

#### Recreate an existing one

```
cd $INSTALL_DASHBOARD_PATH/solr
bin/solr delete -p 8984 -c data-official
bin/solr create -p 8984 -c data-official -d $INSTALL_DASHBOARD_PATH/daobssrc/solr/src/main/solr-cores/data/
```


#### Update configuration

```
cd $INSTALL_DASHBOARD_PATH/solr
server/scripts/cloud-scripts/zkcli.sh -z localhost:9984 -cmd upconfig -confdir $INSTALL_DASHBOARD_PATH/daobssrc/solr/src/main/solr-cores/data/conf -confname data
```

