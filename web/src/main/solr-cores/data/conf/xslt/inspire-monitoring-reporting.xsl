<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:monitoring="http://inspire.jrc.ec.europa.eu/monitoringreporting/monitoring"
  xmlns:solr="java:org.daobs.index.SolrRequestBean"
  xmlns:saxon="http://saxon.sf.net/"
  extension-element-prefixes="saxon"
  exclude-result-prefixes="#all"
  version="2.0">
  
  <xsl:variable name="reportingYear" select="/monitoring:Monitoring/documentYear/year"/>
  <xsl:variable name="reportingTerritory" select="/monitoring:Monitoring/memberState"/>

  <!-- Format date properly. Sometimes month is written
  using one character or two. Prepend 0 when needed. -->
  <xsl:variable name="month" select="/monitoring:Monitoring/MonitoringMD/monitoringDate/month"/>
  <xsl:variable name="reportingDateMonth"
                select="if (string-length($month) = 1) then
                        concat('0', $month) else $month"/>

  <!-- Same for days. -->
  <xsl:variable name="day" select="/monitoring:Monitoring/MonitoringMD/monitoringDate/day"/>
  <xsl:variable name="reportingDateDay"
                select="if (string-length($day) = 1) then
                        concat('0', $day) else $day"/>

  <xsl:variable name="reportingDateSubmission" select="concat(
    /monitoring:Monitoring/MonitoringMD/monitoringDate/year, 
    '-', $reportingDateMonth,
    '-', $reportingDateDay, 'T12:00:00Z')"/>

  <xsl:variable name="reportingDate" select="concat(
    $reportingYear,
    '-12-31T12:00:00Z')"/>
  <xsl:template match="/">
    <add>
      <xsl:apply-templates select="//Indicators/*|
                //RowData/SpatialDataService/NetworkService/userRequest|
                //RowData/SpatialDataSet/Coverage/(relevantArea|actualArea)"/>
    </add>
  </xsl:template>
  
  <xsl:template match="Indicators/*">
    <xsl:variable name="indicatorType" select="local-name()"/>
    <xsl:for-each select="descendant::*[count(*) = 0 and text() != '']">
      <xsl:variable name="indicatorIdentifier" select="local-name()"/>
      <doc>
        <field name="id"><xsl:value-of
                select="concat('indicator', $indicatorIdentifier,
                $reportingDate, $reportingTerritory)"/></field>
        <field name="documentType">indicator</field>
        <field name="indicatorType"><xsl:value-of select="$indicatorType"/></field>
        <field name="indicatorName"><xsl:value-of select="$indicatorIdentifier"/></field>
        <xsl:if test="text() != ''">
          <field name="indicatorValue"><xsl:value-of select="text()"/></field>
        </xsl:if>
        <field name="territory"><xsl:value-of select="$reportingTerritory"/></field>
        <field name="reportingDateSubmission"><xsl:value-of select="$reportingDateSubmission"/></field>
        <field name="reportingDate"><xsl:value-of select="$reportingDate"/></field>
        <field name="reportingYear"><xsl:value-of select="$reportingYear"/></field>
      </doc>
    </xsl:for-each>
  </xsl:template>


  <!-- Ancillary information are extra information not
  available in the metadata records that need to be
  added to the reporting. Those information are provided
  by using the monitoring format and indexed as documentType:ai
  and then used to generate the reporting. -->

  <!-- User requests on each service types
   documentType:ai
   indicatorName:userRequestview
   indicatorValue:100
   territory:be
   Dates will be indexed but for the time being no history
   of the ancillary information will be maintained (ie.
   stats will be provided on all ai).-->
  <xsl:template match="RowData/SpatialDataService/NetworkService/userRequest">
    <xsl:variable name="serviceType"
                  select="../NnServiceType/text()"/>
    <xsl:variable name="indicatorIdentifier"
                  select="concat(local-name(), $serviceType, ../../uuid/text())"/>
    <doc>
      <field name="id"><xsl:value-of
              select="concat('ai', $indicatorIdentifier,
              $reportingDate, $reportingTerritory)"/></field>
      <field name="documentType">ai</field>
      <field name="indicatorName"><xsl:value-of select="$indicatorIdentifier"/></field>
      <xsl:if test="text() != ''">
        <field name="indicatorValue"><xsl:value-of select="text()"/></field>
      </xsl:if>
      <field name="territory"><xsl:value-of select="$reportingTerritory"/></field>
      <field name="reportingDateSubmission"><xsl:value-of select="$reportingDateSubmission"/></field>
      <field name="reportingDate"><xsl:value-of select="$reportingDate"/></field>
      <field name="reportingYear"><xsl:value-of select="$reportingYear"/></field>
    </doc>
  </xsl:template>


  <!-- Actual / Relevant area-->
  <xsl:template match="RowData/SpatialDataSet/Coverage/*">
    <xsl:variable name="indicatorIdentifier"
                  select="local-name()"/>
    <doc>
      <field name="id"><xsl:value-of
              select="concat('ai', $indicatorIdentifier,
              $reportingDate, $reportingTerritory, ../../uuid/text())"/></field>
      <field name="documentType">ai</field>
      <field name="indicatorName"><xsl:value-of select="$indicatorIdentifier"/></field>
      <xsl:if test="text() != ''">
        <field name="indicatorValue"><xsl:value-of select="text()"/></field>
      </xsl:if>
      <field name="territory"><xsl:value-of select="$reportingTerritory"/></field>
      <field name="reportingDateSubmission"><xsl:value-of select="$reportingDateSubmission"/></field>
      <field name="reportingDate"><xsl:value-of select="$reportingDate"/></field>
      <field name="reportingYear"><xsl:value-of select="$reportingYear"/></field>

      <xsl:for-each select="../../Themes/*[text() != '']">
        <xsl:variable name="inspireTheme" as="xs:string"
                      select="solr:analyzeField('inspireTheme_syn', text())"/>

        <xsl:if test="$inspireTheme != ''">
          <field name="inspireTheme_syn"><xsl:value-of select="text()"/></field>
          <field name="inspireTheme"><xsl:value-of select="$inspireTheme"/></field>

          <xsl:if test="position() = 1">
            <field name="inspireAnnex">
              <xsl:value-of select="solr:analyzeField('inspireAnnex_syn', $inspireTheme)"/>
            </field>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </doc>
  </xsl:template>
</xsl:stylesheet>