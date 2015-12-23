<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0">

  <xsl:param name="serviceidentifier"/>

  <xsl:template match="/">
    <add commitWithin="10000">
      <xsl:if test="$serviceidentifier != '' and count(//result/doc/arr[@name = 'inspireTheme']/str) > 0">
        <doc>
          <field name="id"><xsl:value-of select="$serviceidentifier"/></field>
          <xsl:for-each select="distinct-values(//result/doc/arr[@name = 'inspireTheme']/str)">
            <field name="inspireTheme" update="add"><xsl:value-of select="."/></field>
          </xsl:for-each>
          <xsl:for-each select="distinct-values(//result/doc/arr[@name = 'inspireAnnex']/str)">
            <field name="inspireAnnex" update="add"><xsl:value-of select="."/></field>
          </xsl:for-each>
        </doc>
      </xsl:if>
    </add>
  </xsl:template>
</xsl:stylesheet>