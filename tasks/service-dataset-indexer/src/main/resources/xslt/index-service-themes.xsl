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

  <xsl:output omit-xml-declaration="yes"/>

  <xsl:template match="/">
    <xsl:if
      test="$serviceIdentifier != '' and count(//result/doc/arr[@name = 'inspireTheme']/str) > 0">
      <doc>
        <field name="id"><xsl:value-of select="$serviceIdentifier"/></field>
        <xsl:for-each
          select="distinct-values(//result/doc/arr[@name = 'inspireTheme']/str)">
          <field name="inspireTheme" update="add"><xsl:value-of select="."/></field>
        </xsl:for-each>
        <xsl:for-each
          select="distinct-values(//result/doc/arr[@name = 'inspireAnnex']/str)">
          <field name="inspireAnnex" update="add"><xsl:value-of select="."/></field>
        </xsl:for-each>
      </doc>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
