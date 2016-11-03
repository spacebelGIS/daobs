<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright 2014-2016 European Environment Agency

    Licensed under the EUPL, Version 1.1 or – as soon
    they will be approved by the European Commission -
    subsequent versions of the EUPL (the "Licence");
    You may not use this work except in compliance
    with the Licence.
    You may obtain a copy of the Licence at:

    https://joinup.ec.europa.eu/community/eupl/og_page/eupl

    Unless required by applicable law or agreed to in
    writing, software distributed under the Licence is
    distributed on an "AS IS" basis,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied.
    See the Licence for the specific language governing
    permissions and limitations under the Licence.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:gmd="http://www.isotc211.org/2005/gmd"
                xmlns:gco="http://www.isotc211.org/2005/gco"
                xmlns:saxon="http://saxon.sf.net/"
                extension-element-prefixes="saxon"
                exclude-result-prefixes="#all"
                version="2.0">


  <!-- For each record, the main mode 'index' is called,
  then in the document node the mode 'index-extra-fields'
  could be used to index more fields. -->
  <xsl:template mode="index-extra-fields" match="gmd:MD_Metadata">

    <xsl:if
      test="contains(gmd:metadataStandardName/gco:CharacterString, 'Emodnet')">
      <xsl:call-template name="medsea-index-keyword">
        <xsl:with-param name="thesaurusName"
                        select="'Data delivery mechanisms'"/>
        <xsl:with-param name="fieldName"
                        select="'extra_medsea_dataDeliveryMechanism'"/>
      </xsl:call-template>

      <xsl:for-each select="gmd:identificationInfo/*/
                              gmd:resourceConstraints/*/
                                gmd:otherConstraints/*">
        <xsl:element name="extra_medsea_dataPolicy">
          <xsl:value-of select="text()"/>
        </xsl:element>
      </xsl:for-each>

      <xsl:for-each select="gmd:identificationInfo/*/
                              gmd:resourceConstraints/*/
                                gmd:useLimitation/*">
        <xsl:element name="extra_medsea_costBasis">
          <xsl:value-of select="text()"/>
        </xsl:element>
      </xsl:for-each>

      <xsl:for-each select="gmd:dataQualityInfo/*/
                              gmd:report/gmd:DQ_DomainConsistency[gmd:nameOfMeasure/gco:CharacterString = 'Responsiveness']/
                              gmd:result/gmd:DQ_QuantitativeResult/gmd:value/*">
        <xsl:element name="extra_medsea_responsiveness">
          <xsl:value-of select="text()"/>
        </xsl:element>
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
      <xsl:element name="{$fieldName}">
        <xsl:value-of select="text()"/>
      </xsl:element>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
