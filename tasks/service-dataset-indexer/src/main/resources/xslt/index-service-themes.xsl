<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0">

  <xsl:param name="serviceIdentifier"/>

  <xsl:output omit-xml-declaration="yes"/>

  <xsl:template match="/">
    <xsl:if test="$serviceIdentifier != '' and count(//result/doc/arr[@name = 'inspireTheme']/str) > 0">
      <doc>
        <field name="id"><xsl:value-of select="$serviceIdentifier"/></field>
        <xsl:for-each select="distinct-values(//result/doc/arr[@name = 'inspireTheme']/str)">
          <field name="inspireTheme" update="add"><xsl:value-of select="."/></field>
        </xsl:for-each>
        <xsl:for-each select="distinct-values(//result/doc/arr[@name = 'inspireAnnex']/str)">
          <field name="inspireAnnex" update="add"><xsl:value-of select="."/></field>
        </xsl:for-each>
      </doc>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>