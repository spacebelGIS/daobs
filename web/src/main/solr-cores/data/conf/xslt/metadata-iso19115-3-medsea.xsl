<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:cat="http://standards.iso.org/iso/19115/-3/cat/1.0"
                xmlns:cit="http://standards.iso.org/iso/19115/-3/cit/1.0"
                xmlns:gcx="http://standards.iso.org/iso/19115/-3/gcx/1.0"
                xmlns:gex="http://standards.iso.org/iso/19115/-3/gex/1.0"
                xmlns:lan="http://standards.iso.org/iso/19115/-3/lan/1.0"
                xmlns:srv="http://standards.iso.org/iso/19115/-3/srv/2.0"
                xmlns:mas="http://standards.iso.org/iso/19115/-3/mas/1.0"
                xmlns:mcc="http://standards.iso.org/iso/19115/-3/mcc/1.0"
                xmlns:mco="http://standards.iso.org/iso/19115/-3/mco/1.0"
                xmlns:mda="http://standards.iso.org/iso/19115/-3/mda/1.0"
                xmlns:mdb="http://standards.iso.org/iso/19115/-3/mdb/1.0"
                xmlns:mds="http://standards.iso.org/iso/19115/-3/mds/1.0"
                xmlns:mdt="http://standards.iso.org/iso/19115/-3/mdt/1.0"
                xmlns:mex="http://standards.iso.org/iso/19115/-3/mex/1.0"
                xmlns:mmi="http://standards.iso.org/iso/19115/-3/mmi/1.0"
                xmlns:mpc="http://standards.iso.org/iso/19115/-3/mpc/1.0"
                xmlns:mrc="http://standards.iso.org/iso/19115/-3/mrc/1.0"
                xmlns:mrd="http://standards.iso.org/iso/19115/-3/mrd/1.0"
                xmlns:mri="http://standards.iso.org/iso/19115/-3/mri/1.0"
                xmlns:mrl="http://standards.iso.org/iso/19115/-3/mrl/1.0"
                xmlns:mrs="http://standards.iso.org/iso/19115/-3/mrs/1.0"
                xmlns:msr="http://standards.iso.org/iso/19115/-3/msr/1.0"
                xmlns:mdq="http://standards.iso.org/iso/19157/-2/mdq/1.0"
                xmlns:mac="http://standards.iso.org/iso/19115/-3/mac/1.0"
                xmlns:gco="http://standards.iso.org/iso/19115/-3/gco/1.0"
                xmlns:gml="http://www.opengis.net/gml/3.2"
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
  <xsl:template mode="index-extra-fields" match="mdb:MD_Metadata">

    <xsl:if test="contains(mdb:metadataStandard/cit:CI_Citation/cit:title/gco:CharacterString, 'MedSea')">
      <xsl:call-template name="medsea-index-keyword">
        <xsl:with-param name="thesaurusName" select="'Data delivery mechanisms'"/>
        <xsl:with-param name="fieldName" select="'extra_medsea_dataDeliveryMechanism'"/>
      </xsl:call-template>

      <xsl:for-each select="mdb:identificationInfo/*/
                              mri:resourceConstraints/*/
                                mco:otherConstraints/*">
        <field name="extra_medsea_dataPolicy"><xsl:value-of select="text()"/></field>
      </xsl:for-each>

      <xsl:for-each select="mdb:identificationInfo/*/
                              mri:resourceConstraints/*/
                                mri:useLimitation/*">
        <field name="extra_medsea_costBasis"><xsl:value-of select="text()"/></field>
      </xsl:for-each>

      <xsl:for-each select="mdb:dataQualityInfo/*/
                              mdq:report/mdq:DQ_DomainConsistency[mdq:nameOfMeasure/gco:CharacterString = 'Responsiveness']/
                              mdq:result/mdq:DQ_QuantitativeResult/mdq:value/*">
        <field name="extra_medsea_responsiveness"><xsl:value-of select="text()"/></field>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>

  <xsl:template name="medsea-index-keyword">
    <xsl:param name="thesaurusName" as="xs:string"/>
    <xsl:param name="fieldName" as="xs:string"/>

    <xsl:for-each
      select="mdb:identificationInfo/*/
               mri:descriptiveKeywords/mri:MD_Keywords[contains(
                   mri:thesaurusName[1]/cit:CI_Citation/
                     cit:title[1]/gco:CharacterString/text(),
                     $thesaurusName)]/mri:keyword/gco:CharacterString">
      <field name="{$fieldName}"><xsl:value-of select="text()"/></field>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>