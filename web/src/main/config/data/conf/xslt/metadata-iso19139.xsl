<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:gmd="http://www.isotc211.org/2005/gmd"
  xmlns:gco="http://www.isotc211.org/2005/gco"
  xmlns:gmx="http://www.isotc211.org/2005/gmx"
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:gml="http://www.opengis.net/gml" 
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:ns3="http://www.w3.org/2001/SMIL20/" 
  xmlns:ns9="http://www.w3.org/2001/SMIL20/Language" 
  xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" 
  xmlns:dct="http://purl.org/dc/terms/" 
  xmlns:ogc="http://www.opengis.net/ogc" 
  xmlns:ows="http://www.opengis.net/ows" 
  xmlns:gn="http://www.fao.org/geonetwork" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
  exclude-result-prefixes="#all"
  version="2.0">
  
  <xsl:template match="/">
    <add>
      <xsl:for-each select="//gmd:MD_Metadata">
        <xsl:variable name="identifier" select="gmd:fileIdentifier/gco:CharacterString"/>
        <xsl:message>#<xsl:value-of select="$identifier"/></xsl:message>
       <doc>
         <field name="id"><xsl:value-of select="gmd:fileIdentifier/gco:CharacterString"/></field>
         <field name="documentType">metadata</field>
         <field name="country"></field>
         
         <xsl:for-each select="gmd:dateStamp/*">
           <field name="dateStamp"><xsl:value-of select="."/></field>
         </xsl:for-each>
         
         <xsl:for-each select="gmd:language/gco:CharacterString
           |gmd:language/gmd:LanguageCode/@codeListValue
           |gmd:locale/gmd:PT_Locale/gmd:languageCode/gmd:LanguageCode/@codeListValue">
           <field name="mainLanguage"><xsl:value-of select="."/></field>
         </xsl:for-each>
         
         <xsl:for-each select="gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue[.!='']">
           <field name="resourceType"><xsl:value-of select="."/></field>
         </xsl:for-each>
         
         
         <!--<xsl:for-each select="gmd:contact/*/gmd:organisationName/gco:CharacterString|gmd:contact/*/gmd:organisationName/gmx:Anchor">
           <field name="md_poc_organisation"><xsl:value-of select="."/></field>
         </xsl:for-each>-->
         
         <field name="resourceTitle"><xsl:value-of select="gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/></field>
         
         <field name="harvestedDate">2014-06-30T12:00:00Z</field>
         
         <xsl:for-each
           select="gmd:identificationInfo/*/gmd:descriptiveKeywords
                       /gmd:MD_Keywords[contains(gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString/text(), 'GEMET - INSPIRE themes')]
                      /gmd:keyword/gco:CharacterString">
           <field name="inspireThemeEn"><xsl:value-of select="text()"/></field>
         </xsl:for-each>
       </doc>
      </xsl:for-each>
    </add>
  </xsl:template>
</xsl:stylesheet>