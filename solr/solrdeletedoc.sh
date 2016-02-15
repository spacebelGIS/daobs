#!/bin/bash

curl http://localhost:8984/solr/data/update \
    --data '<delete><query>*:*</query></delete>' \
    -H 'Content-type:text/xml; charset=utf-8'

curl http://localhost:8984/solr/data/update \
    --data '<commit/>' \
    -H 'Content-type:text/xml; charset=utf-8'
