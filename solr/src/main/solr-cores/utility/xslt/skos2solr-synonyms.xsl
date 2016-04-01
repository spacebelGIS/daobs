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
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                version="2.0">

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
    }
  </xsl:template>

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
