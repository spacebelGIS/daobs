# ETF Validation Checker

This tasks process the links in the service metadata to validate them with ETF 1.4.1 (http://www.geostandaarden.nl/validatie/inspire/versies/1.4/ETF1.4.1.zip). The validation information is stored in SOLR.

## Requirements
The following components are required by this task:
* Ant
* Java 7
* ETF 1.4.1


Solr MUST be running in port 8984, with metadata records. See [solr/README.md](../../solr/README.md) for configuration.

## ETF Installation

### Manual installation

Unzip the downloaded file `ETF1.4.1.zip` to a folder, for example:
```
wget http://www.geostandaarden.nl/validatie/inspire/versies/1.4/ETF1.4.1.zip
unzip ETF1.4.1.zip -d /opt
```
That creates the following folder `/opt/ETF1.4.1`

### Installation with maven

```
cd tasks/etf-validation-checker
mvn install -Petf-download
```


## Configuration
The configuration is done in the file `eft-validation-checker/src/main/resources/WEB-INF/config.properties`:

* task.validation-etf-checker.validator.path: Path to ETF tool, for example:
```
task.validation-etf-checker.validator.path=/opt/ETF1.4.1/ETF
```

* task.validation-etf-checker.filter: Filter to query the metadata to process. Configured by default to retrieve the service metadata:

```
task.validation-etf-checker.filter=+documentType:metadata +resourceType:service
```

## Execution
To run the task:
```
cd eft-validation-checker
$ mvn camel:run
```