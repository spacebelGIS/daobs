<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:monitoring="http://inspire.jrc.ec.europa.eu/monitoringreporting/monitoring"
  exclude-result-prefixes="xs"
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
      <xsl:apply-templates select="//Indicators/*"/>
    </add>
  </xsl:template>
  
  <xsl:template match="Indicators/*">
    <xsl:variable name="indicatorType" select="local-name()"/>
    <xsl:for-each select="descendant::*[count(*) = 0]">
      <xsl:variable name="indicatorIdentifier" select="local-name()"/>
      <doc>
        <field name="id"><xsl:value-of
                select="concat('indicator', $indicatorIdentifier,
                $reportingDate, $reportingTerritory)"/></field>
        <field name="documentType">indicator</field>
        <field name="indicatorType"><xsl:value-of select="$indicatorType"/></field>
        <field name="indicatorName"><xsl:value-of select="$indicatorIdentifier"/></field>
        <field name="indicatorValue"><xsl:value-of select="text()"/></field>
        <field name="territory"><xsl:value-of select="$reportingTerritory"/></field>
        <field name="reportingDateSubmission"><xsl:value-of select="$reportingDateSubmission"/></field>
        <field name="reportingDate"><xsl:value-of select="$reportingDate"/></field>
        <field name="reportingYear"><xsl:value-of select="$reportingYear"/></field>
      </doc>
    </xsl:for-each>
    
  </xsl:template>
</xsl:stylesheet>