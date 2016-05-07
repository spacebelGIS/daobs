# Download INSPIRE themes in SKOS format
curl -o inspire-theme.rdf \
    https://raw.githubusercontent.com/geonetwork/util-gemet/master/thesauri/inspire-theme.rdf


# Convert SKOS to JSON
# Following the Solr Map format
# See http://lucidworks.com/blog/introducing-solrs-restmanager-and-managed-stop-words-and-synonyms/

java  -jar /home/francois/dev/daobs/web/target/solr/WEB-INF/lib/saxon-9.1.0.8b-patch.jar \
    -s:inspire-theme.rdf -xsl:xslt/skos2solr-synonyms.xsl -o:inspire-theme.json

# Load synonyms in Solr data core
curl -X PUT -H 'Content-type:application/json' \
  -d @inspire-theme.json \
  "http://localhost:8983/solr/data/schema/analysis/synonyms/inspire-theme"


  curl -X PUT -H 'Content-type:application/json' \
    --data-binary '{"mad":["angry","upset"]}' \
  "http://localhost:8983/solr/data/schema/analysis/synonyms/inspire-theme"