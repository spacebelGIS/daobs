<?xml version="1.0"?>
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
                version="2.0">

  <xsl:param name="serviceIdentifier"/>
  <xsl:param name="index"/>
  <xsl:param name="type"/>

  <xsl:output method="text"/>

  <!-- Preserve formatting for building JSON on 2 lines without extra spaces. -->
  <xsl:template match="/"><xsl:if
      test="$serviceIdentifier != '' and count(//inspireTheme) > 0">{"update": {"_index": "<xsl:value-of
      select="$index"/>", "_type": "<xsl:value-of
      select="$type"/>", "_id" : "<xsl:value-of
      select="$serviceIdentifier"/>"}}
{"script": { "inline": "<xsl:for-each
      select="distinct-values(//inspireTheme//text())">ctx._source.inspireTheme.add(\"<xsl:value-of
      select="."/>\");</xsl:for-each><xsl:for-each
      select="distinct-values(//inspireAnnex//text())">ctx._source.inspireAnnex.add(\"<xsl:value-of
      select="."/>\");</xsl:for-each>"}}</xsl:if>
  </xsl:template>
</xsl:stylesheet>
