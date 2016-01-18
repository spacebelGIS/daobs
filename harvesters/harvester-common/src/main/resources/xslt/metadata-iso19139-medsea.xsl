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



  <!-- For each record, the main mode 'index' is called,
  then in the document node the mode 'index-extra-fields'
  could be used to index more fields. -->
  <xsl:template mode="index-extra-fields" match="gmd:MD_Metadata">

    <xsl:if test="contains(gmd:metadataStandardName/gco:CharacterString, 'Emodnet')">
      <xsl:call-template name="medsea-index-keyword">
        <xsl:with-param name="thesaurusName" select="'Data delivery mechanisms'"/>
        <xsl:with-param name="fieldName" select="'extra_medsea_dataDeliveryMechanism'"/>
      </xsl:call-template>

      <xsl:for-each select="gmd:identificationInfo/*/
                              gmd:resourceConstraints/*/
                                gmd:otherConstraints/*">
        <field name="extra_medsea_dataPolicy"><xsl:value-of select="text()"/></field>
      </xsl:for-each>

      <xsl:for-each select="gmd:identificationInfo/*/
                              gmd:resourceConstraints/*/
                                gmd:useLimitation/*">
        <field name="extra_medsea_costBasis"><xsl:value-of select="text()"/></field>
      </xsl:for-each>

      <xsl:for-each select="gmd:dataQualityInfo/*/
                              gmd:report/gmd:DQ_DomainConsistency[gmd:nameOfMeasure/gco:CharacterString = 'Responsiveness']/
                              gmd:result/gmd:DQ_QuantitativeResult/gmd:value/*">
        <field name="extra_medsea_responsiveness"><xsl:value-of select="text()"/></field>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="medsea-index-keyword">
    <xsl:param name="thesaurusName" as="xs:string"/>
    <xsl:param name="fieldName" as="xs:string"/>

    <xsl:for-each
      select="gmd:identificationInfo/*/
               gmd:descriptiveKeywords/gmd:MD_Keywords[contains(
                   gmd:thesaurusName[1]/gmd:CI_Citation/
                     gmd:title[1]/gco:CharacterString/text(),
                     $thesaurusName)]/gmd:keyword/gco:CharacterString">
      <field name="{$fieldName}"><xsl:value-of select="text()"/></field>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>