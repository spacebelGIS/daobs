<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">

  <xsl:output method="text" indent="no"/>

  <xsl:template match="/">{
    <xsl:for-each select="//skos:Concept">
      <xsl:for-each-group select="skos:prefLabel[@xml:lang!='en']"
              group-by=".">
        "<xsl:value-of select="current-grouping-key()"/>":
        ["<xsl:value-of select="parent::node()/skos:prefLabel[@xml:lang='en']"/>"],
      </xsl:for-each-group>
      "<xsl:value-of select="@rdf:about"/>":
      ["<xsl:value-of select="skos:prefLabel[@xml:lang='en']"/>"]
      <xsl:if test="position() != last()">,</xsl:if>
    </xsl:for-each>
  }</xsl:template>

  <!--<xsl:template match="/">{
    <xsl:for-each select="//skos:Concept">
      "<xsl:value-of select="skos:prefLabel[@xml:lang='en']"/>":
      [
      <xsl:for-each select="skos:prefLabel">
        "<xsl:value-of select="."/>",
      </xsl:for-each>
      "<xsl:value-of select="@rdf:about"/>"
      ]
      <xsl:if test="position() != last()">,</xsl:if>
    </xsl:for-each>
    }</xsl:template>-->
</xsl:stylesheet>