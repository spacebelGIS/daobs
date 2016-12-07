## Install, configure and start Kibana

### Manual installation

Download Kibana from https://www.elastic.co/fr/downloads/kibana

Set Kibana base path and index name in config/kibana.yml

```
server.basePath: "/<webappname>/dashboard"

kibana.index: ".dashboards"

```

Start Kibana.

Import configuration

```
curl -X PUT http://localhost:9200/.dashboard -d @config/idx-pattern-indicators.json
curl -X PUT http://localhost:9200/.dashboard -d @config/idx-pattern-records.json
curl -X PUT http://localhost:9200/.dashboard -d @config/dashboards.json
```


### Maven installation

Maven could take care of the installation steps:
* download
* initialize collection
* start

Use the following commands:

```
cd dashboards
mvn install -Pkb-download
mvn exec:exec -Dkb-start
```

### Production use

Configure Kibana to start on server startup.


