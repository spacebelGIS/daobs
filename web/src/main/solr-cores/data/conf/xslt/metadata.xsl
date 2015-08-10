<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:gmd="http://www.isotc211.org/2005/gmd"
                xmlns:gco="http://www.isotc211.org/2005/gco"
                xmlns:srv="http://www.isotc211.org/2005/srv"
                xmlns:gmx="http://www.isotc211.org/2005/gmx"
                xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:daobs="http://daobs.org"
                xmlns:gml="http://www.opengis.net/gml"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:ns3="http://www.w3.org/2001/SMIL20/"
                xmlns:ns9="http://www.w3.org/2001/SMIL20/Language"
                xmlns:dct="http://purl.org/dc/terms/"
                xmlns:ogc="http://www.opengis.net/ogc"
                xmlns:ows="http://www.opengis.net/ows"
                xmlns:gn="http://www.fao.org/geonetwork"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:solr="java:org.daobs.index.SolrRequestBean"
                xmlns:saxon="http://saxon.sf.net/"
                extension-element-prefixes="saxon"
                exclude-result-prefixes="#all"
                version="2.0">

  <xsl:output name="default-serialize-mode"
              indent="no"
              omit-xml-declaration="yes" />

  <xsl:variable name="harvester" as="element()?"
                select="/harvestedContent/daobs:harvester"/>

  <xsl:variable name="pointOfTruthURLPattern" as="xs:string?"
                select="normalize-space($harvester/daobs:pointOfTruthURLPattern)"/>

  <xsl:variable name="dateFormat" as="xs:string"
                select="'[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01]Z'"/>

  <xsl:variable name="separator" as="xs:string"
                select="'|'"/>

  <!-- To avoid Document contains at least one immense term
  in field="resourceAbstract" (whose UTF8 encoding is longer
  than the max length 32766. -->
  <xsl:variable name="maxFieldLength" select="32000" as="xs:integer"/>

  <xsl:include href="metadata-iso19139.xsl"/>
  <xsl:include href="metadata-iso19115-3.xsl"/>


  <xsl:template match="/">
    <!-- Add a Solr document -->
    <add>
      <!-- For any ISO19139 records in the input XML document
      Some records from IS do not have record identifier. Ignore them.
      -->
      <xsl:variable name="records"
                    select="//gmd:MD_Metadata[gmd:fileIdentifier/gco:CharacterString != '']"/>

      <!-- Check number of records returned and reported -->
      <xsl:message>======================================================</xsl:message>
      <xsl:message>DEBUG: <xsl:value-of select="normalize-space($harvester/daobs:url)"/>.</xsl:message>
      <xsl:message>DEBUG: <xsl:value-of select="//csw:SearchResults/@numberOfRecordsReturned"/> record(s) returned in CSW response.</xsl:message>
      <xsl:message>DEBUG: <xsl:value-of select="count($records)"/> record(s) to index.</xsl:message>

      <!-- Report error on record with null UUID -->
      <xsl:variable name="recordsWithNullUUID"
                    select="//gmd:MD_Metadata[gmd:fileIdentifier/gco:CharacterString = ''
                            or not(gmd:fileIdentifier)]"/>
      <xsl:variable name="numberOfRecordsWithNullUUID"
                    select="count($recordsWithNullUUID)"/>

      <xsl:if test="$numberOfRecordsWithNullUUID > 0">
        <xsl:message>WARNING: <xsl:value-of select="$numberOfRecordsWithNullUUID"/> record(s) with null UUID.</xsl:message>
        <xsl:message><xsl:copy-of select="$recordsWithNullUUID"/></xsl:message>
      </xsl:if>


      <!-- Check duplicates -->
      <xsl:for-each select="$records">
        <xsl:variable name="identifier" as="xs:string"
                      select="gmd:fileIdentifier/gco:CharacterString[. != '']"/>
        <xsl:variable name="numberOfRecordWithThatUUID"
                      select="count(../*[gmd:fileIdentifier/gco:CharacterString = $identifier])"/>
        <xsl:if test="$numberOfRecordWithThatUUID > 1">
          <xsl:message>WARNING: <xsl:value-of select="$numberOfRecordWithThatUUID"/> record(s) having UUID '<xsl:value-of select="$identifier"/>' in that set.</xsl:message>
        </xsl:if>
      </xsl:for-each>


      <xsl:for-each select="$records">
        <xsl:apply-templates mode="index" select="."/>
      </xsl:for-each>
    </add>
  </xsl:template>

  <!-- For each record, the main mode 'index' is called,
  then in the document node the mode 'index-extra-fields'
  could be used to index more fields. -->
  <xsl:template mode="index-extra-fields" match="*"/>

  <!-- then after the doc, the 'index-extra-documents' mode
  could be used to create more doc. -->
  <xsl:template mode="index-extra-documents" match="*"/>

</xsl:stylesheet>