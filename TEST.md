## Testing INSPIRE monitoring



* Valid/Invalid data set metadata
```
Solr Query:
http://localhost:8983/data/select?q=%2Bterritory%3ANL+%2BresourceType%3Adataset&start=0&rows=0&wt=json&indent=true&facet=true&facet.field=isAboveThreshold

XPath Query:
http://localhost:8983/daobs/reporting/custom/inspire/NL?fq=%20%2Bterritory%3ANL&withRowData=true&organizationName=&email=&language=
* Valid data set
count(//*:IRConformity)
* Invalid data set
count(//*:SpatialDataSet[not(*:MdDataSetExistence/*:IRConformity)])
```



* Valid/Invalid data set metadata
```
Solr Query:
http://localhost:8983/data/select?q=%2Bterritory%3ANL+%2BresourceType%3Aservice&start=0&rows=0&wt=json&indent=true&facet=true&facet.field=isAboveThreshold

XPath Query:
http://localhost:8983/daobs/reporting/custom/inspire/NL?fq=%20%2Bterritory%3ANL&withRowData=true&organizationName=&email=&language=
* Valid service
count(//*:SpatialDataService[*:MdServiceExistence/*:mdConformity = 'true'])
* Invalid service
count(//*:SpatialDataService[*:MdServiceExistence/*:mdConformity = 'false'])
```





