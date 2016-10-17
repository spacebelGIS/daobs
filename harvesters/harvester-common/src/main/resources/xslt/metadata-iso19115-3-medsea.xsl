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
                xmlns:cit="http://standards.iso.org/iso/19115/-3/cit/1.0"
                xmlns:mco="http://standards.iso.org/iso/19115/-3/mco/1.0"
                xmlns:mdb="http://standards.iso.org/iso/19115/-3/mdb/1.0"
                xmlns:mri="http://standards.iso.org/iso/19115/-3/mri/1.0"
                xmlns:mcc="http://standards.iso.org/iso/19115/-3/mcc/1.0"
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


    <xsl:if
      test="contains(mdb:metadataStandard/cit:CI_Citation/cit:title/gco:CharacterString, 'Emodnet')">

      <xsl:variable name="thesaurusList">
        <entry key="Data delivery mechanisms">extra_medsea_dataDeliveryMechanism</entry>
        <entry key="emodnet-checkpoint.policy.visibility">extra_medsea_policyVisibility</entry>
        <entry key="emodnet-checkpoint.service.extent">extra_medsea_serviceExtent</entry>
        <entry key="emodnet-checkpoint.visibility">extra_medsea_visibility</entry>
        <entry key="emodnet-checkpoint.readyness">extra_medsea_readyness</entry>
      </xsl:variable>

      <xsl:variable name="identification" select="mdb:identificationInfo"/>

      <xsl:for-each select="$thesaurusList/entry">
        <xsl:message>##<xsl:value-of select="."/> </xsl:message>
        <xsl:variable name="thesaurusName" select="@key"/>
        <xsl:variable name="fieldName" select="."/>
        <xsl:for-each
          select="$identification/*/
                 mri:descriptiveKeywords/mri:MD_Keywords[
                 contains(
                     mri:thesaurusName[1]/*/cit:title[1]/gco:CharacterString/text(),
                     $thesaurusName) or
                 contains(
                     mri:thesaurusName[1]/*/cit:identifier[1]/*/mcc:code/*/text(),
                     $thesaurusName)
                     ]/mri:keyword/gco:CharacterString">
          <field name="{$fieldName}">
            <xsl:value-of select="text()"/>
          </field>
        </xsl:for-each>
      </xsl:for-each>

      <xsl:for-each select="mdb:identificationInfo/*/
                              mri:resourceConstraints/*/
                                mco:otherConstraints/*">
        <field name="extra_medsea_dataPolicy">
          <xsl:value-of select="text()"/>
        </field>
      </xsl:for-each>

      <xsl:for-each select="mdb:identificationInfo/*/
                              mri:resourceConstraints/*/
                                mri:useLimitation/*">
        <field name="extra_medsea_costBasis">
          <xsl:value-of select="text()"/>
        </field>
      </xsl:for-each>

      <xsl:for-each select="mdb:dataQualityInfo/*/
                              mdq:report/mdq:DQ_DomainConsistency[mdq:nameOfMeasure/gco:CharacterString = 'Responsiveness']/
                              mdq:result/mdq:DQ_QuantitativeResult/mdq:value/*">
        <field name="extra_medsea_responsiveness">
          <xsl:value-of select="text()"/>
        </field>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
