<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:cit="http://standards.iso.org/iso/19115/-3/cit/1.0"
                xmlns:mco="http://standards.iso.org/iso/19115/-3/mco/1.0"
                xmlns:mdb="http://standards.iso.org/iso/19115/-3/mdb/1.0"
                xmlns:mri="http://standards.iso.org/iso/19115/-3/mri/1.0"
                xmlns:mdq="http://standards.iso.org/iso/19157/-2/mdq/1.0"
                xmlns:gco="http://standards.iso.org/iso/19115/-3/gco/1.0"
                xmlns:saxon="http://saxon.sf.net/"
                extension-element-prefixes="saxon"
                exclude-result-prefixes="#all"
                version="2.0">


  <!-- For each record, the main mode 'index' is called,
  then in the document node the mode 'index-extra-fields'
  could be used to index more fields. -->
  <xsl:template mode="index-extra-fields" match="mdb:MD_Metadata">

    <xsl:if test="contains(mdb:metadataStandard/cit:CI_Citation/cit:title/gco:CharacterString, 'Emodnet')">
      <xsl:variable name="thesaurusName" select="'Data delivery mechanisms'"/>
      <xsl:variable name="fieldName" select="'extra_medsea_dataDeliveryMechanism'"/>
      <xsl:for-each
              select="mdb:identificationInfo/*/
               mri:descriptiveKeywords/mri:MD_Keywords[contains(
                   mri:thesaurusName[1]/cit:CI_Citation/
                     cit:title[1]/gco:CharacterString/text(),
                     $thesaurusName)]/mri:keyword/gco:CharacterString">
        <field name="{$fieldName}"><xsl:value-of select="text()"/></field>
      </xsl:for-each>

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
</xsl:stylesheet>