<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:gmd="http://www.isotc211.org/2005/gmd"
  xmlns:gco="http://www.isotc211.org/2005/gco"
  xmlns:srv="http://www.isotc211.org/2005/srv"
  xmlns:gmx="http://www.isotc211.org/2005/gmx"
  xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:gml="http://www.opengis.net/gml" 
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:ns3="http://www.w3.org/2001/SMIL20/" 
  xmlns:ns9="http://www.w3.org/2001/SMIL20/Language" 
  xmlns:dct="http://purl.org/dc/terms/" 
  xmlns:ogc="http://www.opengis.net/ogc" 
  xmlns:ows="http://www.opengis.net/ows" 
  xmlns:gn="http://www.fao.org/geonetwork" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:solr="java:org.daobs.index.SolrRequestBean"
  xmlns:saxon="http://saxon.sf.net/"
  extension-element-prefixes="saxon"
  exclude-result-prefixes="#all"
  version="2.0">

  <xsl:output name="default-serialize-mode"
              indent="no"
              omit-xml-declaration="yes" />

  <xsl:variable name="harvester" as="element()?"
                select="/harvestedContent/harvester"/>

  <xsl:variable name="dateFormat" as="xs:string"
                select="'[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01]Z'"/>

  <xsl:variable name="separator" as="xs:string"
                select="'|'"/>


  <xsl:variable name="eu10892010">
    <en>Commission Regulation (EU) No 1089/2010 of 23 November 2010 implementing Directive 2007/2/EC of the European Parliament and of the Council as regards interoperability of spatial data sets and services</en>
    <bg>Регламент (ЕС) № 1089/2010 на Комисията от 23 ноември 2010 година за прилагане на Директива 2007/2/ЕО на Европейския парламент и на Съвета по отношение на оперативната съвместимост на масиви от пространствени данни и услуги за пространствени данни</bg>
    <es>Reglamento (UE) n ° 1089/2010 de la Comisión, de 23 de noviembre de 2010 , por el que se aplica la Directiva 2007/2/CE del Parlamento Europeo y del Consejo en lo que se refiere a la interoperabilidad de los conjuntos y los servicios de datos espaciales</es>
    <cs>Nařízení Komise (EU) č. 1089/2010 ze dne 23. listopadu 2010 , kterým se provádí směrnice Evropského parlamentu a Rady 2007/2/ES, pokud jde o interoperabilitu sad prostorových dat a služeb prostorových dat</cs>
    <da>Kommissionens forordning (EU) nr. 1089/2010 af 23. november 2010 om gennemførelse af Europa-Parlamentets og Rådets direktiv 2007/2/EF for så vidt angår interoperabilitet for geodatasæt og -tjenester</da>
    <de>Verordnung (EG) Nr. 1089/2010 der Kommission vom 23. November 2010 zur Durchführung der Richtlinie 2007/2/EG des Europäischen Parlaments und des Rates hinsichtlich der Interoperabilität von Geodatensätzen und -diensten</de>
    <et>Komisjoni määrus (EL) nr 1089/2010, 23. november 2010 , millega rakendatakse Euroopa Parlamendi ja nõukogu direktiivi 2007/2/EÜ seoses ruumiandmekogumite ja -teenuste ristkasutatavusega</et>
    <el>Κανονισμός (ΕΕ) αριθ. 1089/2010 της Επιτροπής, της 23ης Νοεμβρίου 2010 , σχετικά με την εφαρμογή της οδηγίας 2007/2/ΕΚ του Ευρωπαϊκού Κοινοβουλίου και του Συμβουλίου όσον αφορά τη διαλειτουργικότητα των συνόλων και των υπηρεσιών χωρικών δεδομένων</el>
    <fr>Règlement (UE) n ° 1089/2010 de la Commission du 23 novembre 2010 portant modalités d'application de la directive 2007/2/CE du Parlement européen et du Conseil en ce qui concerne l'interopérabilité des séries et des services de données géographiques</fr>
    <hr>Uredba Komisije (EU) br. 1089/2010 od 23. studenoga 2010. o provedbi Direktive 2007/2/EZ Europskog parlamenta i Vijeća o međuoperativnosti skupova prostornih podataka i usluga u vezi s prostornim podacima</hr>
    <it>Regolamento (UE) n. 1089/2010 della Commissione, del 23 novembre 2010 , recante attuazione della direttiva 2007/2/CE del Parlamento europeo e del Consiglio per quanto riguarda l'interoperabilità dei set di dati territoriali e dei servizi di dati territoriali</it>
    <lv>Komisijas Regula (ES) Nr. 1089/2010 ( 2010. gada 23. novembris ), ar kuru īsteno Eiropas Parlamenta un Padomes Direktīvu 2007/2/EK attiecībā uz telpisko datu kopu un telpisko datu pakalpojumu savstarpējo izmantojamību</lv>
    <lt>2010 m. lapkričio 23 d. Komisijos reglamentas (ES) Nr. 1089/2010, kuriuo įgyvendinamos Europos Parlamento ir Tarybos direktyvos 2007/2/EB nuostatos dėl erdvinių duomenų rinkinių ir paslaugų sąveikumo</lt>
    <hu>A Bizottság 1089/2010/EU rendelete ( 2010. november 23. ) a 2007/2/EK európai parlamenti és tanácsi irányelv téradatkészletek és -szolgáltatások interoperabilitására vonatkozó rendelkezéseinek végrehajtásáról</hu>
    <mt>Regolament tal-Kummissjoni (UE) Nru 1089/2010 tat- 23 ta' Novembru 2010 li jimplimenta d-Direttiva 2007/2/KE tal-Parlament Ewropew u tal-Kunsill fir-rigward tal-interoperabbiltà tas-settijiet ta’ dejta u servizzi ġeografiċi</mt>
    <nl>Verordening (EU) nr. 1089/2010 van de Commissie van 23 november 2010 ter uitvoering van Richtlijn 2007/2/EG van het Europees Parlement en de Raad betreffende de interoperabiliteit van verzamelingen ruimtelijke gegevens en van diensten met betrekking tot ruimtelijke gegevens</nl>
    <pl>Rozporządzenie Komisji (UE) nr 1089/2010 z dnia 23 listopada 2010 r. w sprawie wykonania dyrektywy 2007/2/WE Parlamentu Europejskiego i Rady w zakresie interoperacyjności zbiorów i usług danych przestrzennych</pl>
    <pt>Regulamento (UE) n. ° 1089/2010 da Comissão, de 23 de Novembro de 2010 , que estabelece as disposições de execução da Directiva 2007/2/CE do Parlamento Europeu e do Conselho relativamente à interoperabilidade dos conjuntos e serviços de dados geográficos</pt>
    <ro>Regulamentul (UE) nr. 1089/2010 al Comisiei din 23 noiembrie 2010 de punere în aplicare a Directivei 2007/2/CE a Parlamentului European și a Consiliului în ceea ce privește interoperabilitatea seturilor și serviciilor de date spațiale</ro>
    <sk>Nariadenie Komisie (EÚ) č. 1089/2010 z  23. novembra 2010 , ktorým sa vykonáva smernica Európskeho parlamentu a Rady 2007/2/ES, pokiaľ ide o interoperabilitu súborov a služieb priestorových údajov</sk>
    <sl>Uredba Komisije (EU) št. 1089/2010 z dne 23. novembra 2010 o izvajanju Direktive 2007/2/ES Evropskega parlamenta in Sveta glede medopravilnosti zbirk prostorskih podatkov in storitev v zvezi s prostorskimi podatki</sl>
    <fi>Komission asetus (EU) N:o 1089/2010, annettu 23 päivänä marraskuuta 2010 , Euroopan parlamentin ja neuvoston direktiivin 2007/2/EY täytäntöönpanosta paikkatietoaineistojen ja -palvelujen yhteentoimivuuden osalta</fi>
    <sv>Kommissionens förordning (EU) nr 1089/2010 av den 23 november 2010 om genomförande av Europaparlamentets och rådets direktiv 2007/2/EG vad gäller interoperabilitet för rumsliga datamängder och datatjänster</sv>
    <!-- Translation http://eur-lex.europa.eu/legal-content/SV/TXT/?uri=CELEX:32010R1089&qid=1418298723943  -->
  </xsl:variable>

  <xsl:variable name="eu9762009">
    <en>Commission Regulation (EC) No 976/2009 of 19 October 2009 implementing Directive 2007/2/EC of the European Parliament and of the Council as regards the Network Services</en>
    <bg>Регламент (ЕО) № 976/2009 на Комисията от 19 октомври 2009 година за прилагане на Директива 2007/2/ЕО на Европейския парламент и на Съвета по отношение на мрежовите услуги</bg>
    <cs>Nařízení Komise (ES) č. 976/2009 ze dne 19. října 2009 , kterým se provádí směrnice Evropského parlamentu a Rady 2007/2/ES, pokud jde o síťové služby</cs>
    <da>Kommissionens forordning (EF) nr. 976/2009 af 19. oktober 2009 om gennemførelse af Europa-Parlamentets og Rådets direktiv 2007/2/EF for så vidt angår nettjenesterne</da>
    <de>Verordnung (EG) Nr. 976/2009 der Kommission vom 19. Oktober 2009 zur Durchführung der Richtlinie 2007/2/EG des Europäischen Parlaments und des Rates hinsichtlich der Netzdienste</de>
    <et>Komisjoni määrus (EÜ) nr 976/2009, 19. oktoober 2009 , millega rakendatakse Euroopa Parlamendi ja nõukogu direktiivi 2007/2/EÜ seoses võrguteenustega</et>
    <el>Κανονισμός (ΕΚ) αριθ. 976/2009 της Επιτροπής, της 19ης Οκτωβρίου 2009 , για την υλοποίηση της οδηγίας 2007/2/ΕΚ του Ευρωπαϊκού Κοινοβουλίου και του Συμβουλίου όσον αφορά τις δικτυακές υπηρεσίες</el>
    <fr>Règlement (CE) n o  976/2009 de la Commission du 19 octobre 2009 portant modalités d’application de la directive 2007/2/CE du Parlement européen et du Conseil en ce qui concerne les services en réseau</fr>
    <hr>Uredba Komisije (EZ) br. 976/2009 od 19. listopada 2009. o provedbi Direktive 2007/2/EZ Europskog parlamenta i Vijeća u vezi s mrežnim uslugama</hr>
    <it>Regolamento (CE) n. 976/2009 della Commissione, del 19 ottobre 2009 , recante attuazione della direttiva 2007/2/CE del Parlamento europeo e del Consiglio per quanto riguarda i servizi di rete</it>
    <lv>Komisijas Regula (EK) Nr. 976/2009 ( 2009. gada 19. oktobris ), ar kuru īsteno Eiropas Parlamenta un Padomes Direktīvu 2007/2/EK attiecībā uz tīkla pakalpojumiem</lv>
    <lt>2009 m. spalio 19 d. Komisijos reglamentas (EB) Nr. 976/2009, kuriuo įgyvendinamos Europos Parlamento ir Tarybos direktyvos 2007/2/EB nuostatos dėl tinklo paslaugų</lt>
    <hu>A Bizottság 976/2009/EK rendelete ( 2009. október 19. ) a 2007/2/EK európai parlamenti és tanácsi irányelv hálózati szolgáltatásokra vonatkozó rendelkezéseinek végrehajtásáról</hu>
    <mt>Regolament tal-Kummissjoni (KE) Nru 976/2009 tad- 19 ta’ Ottubru 2009 li jimplimenta d-Direttiva 2007/2/KE tal-Parlament Ewropew u tal-Kunsill fir-rigward tas-Servizzi ta’ Netwerk</mt>
    <nl>Verordening (EG) nr. 976/2009 van de Commissie van 19 oktober 2009 tot uitvoering van Richtlijn 2007/2/EG van het Europees Parlement en de Raad wat betreft de netwerkdiensten</nl>
    <pl>Rozporządzenie Komisji (WE) nr 976/2009 z dnia 19 października 2009 r. w sprawie wykonania dyrektywy 2007/2/WE Parlamentu Europejskiego i Rady w zakresie usług sieciowych</pl>
    <pt>Regulamento (CE) n. o  976/2009 da Comissão, de 19 de Outubro de 2009 , que estabelece as disposições de execução da Directiva 2007/2/CE do Parlamento Europeu e do Conselho no que respeita aos serviços de rede</pt>
    <ro>Regulamentul (CE) nr. 976/2009 al Comisiei din 19 octombrie 2009 de aplicare a Directivei 2007/2/CE a Parlamentului European și a Consiliului în ceea ce privește serviciile de rețea</ro>
    <sk>Nariadenie Komisie (ES) č. 976/2009 z  19. októbra 2009 , ktorým sa vykonáva smernica Európskeho parlamentu a Rady 2007/2/ES, pokiaľ ide o sieťové služby</sk>
    <sl>Uredba Komisije (ES) št. 976/2009 z dne 19. oktobra 2009 o izvajanju Direktive 2007/2/ES Evropskega parlamenta in Sveta glede omrežnih storitev</sl>
    <fi>Komission asetus (EY) N:o 976/2009, annettu 19 päivänä lokakuuta 2009 , Euroopan parlamentin ja neuvoston direktiivin 2007/2/EY täytäntöönpanosta verkkopalvelujen osalta</fi>
    <sv>Kommissionens förordning (EG) nr 976/2009 av den 19 oktober 2009 om genomförande av Europaparlamentets och rådets direktiv 2007/2/EG med avseende på nättjänster</sv>
  </xsl:variable>



  <xsl:template match="/">
    <!-- Add a Solr document -->
    <add>
      <!-- For any ISO19139 records in the input XML document
      Some records from IS do not have record identifier. Ignore them.
      -->
      <xsl:variable name="records"
                    select="//gmd:MD_Metadata[gmd:fileIdentifier/gco:CharacterString != '']"/>


      <!-- Check number of records returned and reported -->
      <xsl:message>======================================================</xsl:message>
      <xsl:message>DEBUG: <xsl:value-of select="//csw:SearchResults/@numberOfRecordsReturned"/> record(s) returned in CSW response.</xsl:message>
      <xsl:message>DEBUG: <xsl:value-of select="count($records)"/> record(s) to index.</xsl:message>

      <!-- Report error on record with null UUID -->
      <xsl:variable name="recordsWithNullUUID"
                    select="//gmd:MD_Metadata[gmd:fileIdentifier/gco:CharacterString = ''
                            or not(gmd:fileIdentifier)]"/>
      <xsl:variable name="numberOfRecordsWithNullUUID"
                    select="count($recordsWithNullUUID)"/>

      <xsl:if test="$numberOfRecordsWithNullUUID > 0">
        <xsl:message>WARNING: <xsl:value-of select="$numberOfRecordsWithNullUUID"/> record(s) with null UUID.</xsl:message>
        <xsl:message><xsl:copy-of select="$recordsWithNullUUID"/></xsl:message>
      </xsl:if>


      <!-- Check duplicates -->
      <xsl:for-each select="$records">
        <xsl:variable name="identifier" as="xs:string"
                      select="gmd:fileIdentifier/gco:CharacterString[. != '']"/>
        <xsl:variable name="numberOfRecordWithThatUUID"
                      select="count(../*[gmd:fileIdentifier/gco:CharacterString = $identifier])"/>
        <xsl:if test="$numberOfRecordWithThatUUID > 1">
          <xsl:message>WARNING: <xsl:value-of select="$numberOfRecordWithThatUUID"/> record(s) having UUID '<xsl:value-of select="$identifier"/>' in that set.</xsl:message>
        </xsl:if>
      </xsl:for-each>



      <xsl:for-each select="$records">
        <!-- Main variables for the document -->
        <xsl:variable name="identifier" as="xs:string"
                      select="gmd:fileIdentifier/gco:CharacterString[. != '']"/>

        <xsl:variable name="mainLanguage" as="xs:string?"
                      select="gmd:language/gco:CharacterString[normalize-space(.) != '']|
                          gmd:language/gmd:LanguageCode/
                            @codeListValue[normalize-space(.) != '']"/>

        <xsl:variable name="otherLanguages" as="attribute()*"
                      select="gmd:locale/gmd:PT_Locale/
                            gmd:languageCode/gmd:LanguageCode/
                              @codeListValue[normalize-space(.) != '']"/>

        <!-- Record is dataset if no hierarchyLevel -->
        <xsl:variable name="isDataset" as="xs:boolean"
                      select="
                          count(gmd:hierarchyLevel[gmd:MD_ScopeCode/@codeListValue='dataset']) > 0 or
                          count(gmd:hierarchyLevel) = 0"/>
        <xsl:variable name="isService" as="xs:boolean"
                      select="
                          count(gmd:hierarchyLevel[gmd:MD_ScopeCode/@codeListValue='service']) > 0"/>

        <xsl:message>#<xsl:value-of select="position()"/>. <xsl:value-of select="$identifier"/></xsl:message>

        <!-- Create a first document representing the main record. -->
        <doc>
          <field name="documentType">metadata</field>
          <field name="documentStandard">iso19139</field>

          <!-- Index the metadata document as XML -->
          <field name="document"><xsl:value-of select="saxon:serialize(., 'default-serialize-mode')"/></field>
          <field name="id"><xsl:value-of select="$identifier"/></field>
          <field name="metadataIdentifier"><xsl:value-of select="$identifier"/></field>

          <!-- Harvester details -->
          <field name="territory"><xsl:value-of select="$harvester/territory"/></field>
          <field name="harvesterId"><xsl:value-of select="$harvester/url"/></field>
          <field name="harvestedDate">
            <xsl:value-of select="if ($harvester/date)
                                  then $harvester/date
                                  else format-dateTime(current-dateTime(), $dateFormat)"/>
          </field>


          <!-- Indexing record information -->
          <!-- # Date -->
          <!-- TODO improve date formatting maybe using Joda parser
          Select first one because some records have 2 dates !
          eg. fr-784237539-bdref20100101-0105
          -->
          <xsl:for-each select="gmd:dateStamp/*[text() != '' and position() = 1]">
            <field name="dateStamp">
              <xsl:value-of select="if (name() = 'gco:Date' and string-length(.) = 4)
                                    then concat(., '-01-01T00:00:00Z')
                                    else if (name() = 'gco:Date' and string-length(.) = 7)
                                    then concat(., '-01T00:00:00Z')
                                    else if (name() = 'gco:Date' or string-length(.) = 10)
                                    then concat(., 'T00:00:00Z')
                                    else (
                                      if (ends-with(., 'Z'))
                                      then .
                                      else concat(., 'Z')
                                    )"/>
            </field>
          </xsl:for-each>


          <!-- # Languages -->
          <field name="mainLanguage"><xsl:value-of select="$mainLanguage"/></field>

          <xsl:for-each select="$otherLanguages">
            <field name="otherLanguage"><xsl:value-of select="."/></field>
          </xsl:for-each>


          <!-- # Resource type -->
          <xsl:choose>
            <xsl:when test="$isDataset">
              <field name="resourceType">dataset</field>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="gmd:hierarchyLevel/gmd:MD_ScopeCode/
                                  @codeListValue[normalize-space(.) != '']">
                <field name="resourceType"><xsl:value-of select="."/></field>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>


          <!-- Indexing metadata contact -->
          <xsl:apply-templates mode="index-contact" select="gmd:contact">
            <xsl:with-param name="fieldSuffix" select="''"/>
          </xsl:apply-templates>


          <!-- Indexing resource information
          TODO: Should we support multiple identification in the same record
          eg. nl db60a314-5583-437d-a2ff-1e59cc57704e
          Also avoid error when records contains multiple MD_IdentificationInfo
          or SRV_ServiceIdentification or a mix
          eg. de 8bb5334f-558b-982b-7b12-86ea486540d7
          -->
          <xsl:for-each select="gmd:identificationInfo[1]/*[1]">
            <xsl:for-each select="gmd:citation/gmd:CI_Citation">
              <field name="resourceTitle">
                <xsl:value-of select="gmd:title/gco:CharacterString/text()"/>
              </field>

              <xsl:for-each select="gmd:date/gmd:CI_Date[gmd:date/*/text() != '']">
                <xsl:variable name="dateType"
                              select="gmd:dateType/gmd:CI_DateTypeCode/@codeListValue"
                              as="xs:string?"/>
                <xsl:variable name="date"
                              select="string(gmd:date/gco:Date|gmd:date/gco:DateTime)"/>
                <field name="{$dateType}DateForResource"><xsl:value-of select="$date"/></field>
                <field name="{$dateType}YearForResource"><xsl:value-of select="substring($date, 0, 5)"/></field>
                <field name="{$dateType}MonthForResource"><xsl:value-of select="substring($date, 0, 8)"/></field>
              </xsl:for-each>
            </xsl:for-each>

            <field name="resourceAbstract">
              <xsl:value-of select="gmd:abstract/gco:CharacterString/text()"/>
            </field>


            <!-- Indexing resource contact -->
            <xsl:apply-templates mode="index-contact"
                                 select="gmd:pointOfContact">
              <xsl:with-param name="fieldSuffix" select="'ForResource'"/>
            </xsl:apply-templates>


            <xsl:for-each select="gmd:presentationForm/gmd:CI_PresentationFormCode/@codeListValue[. != '']">
              <field name="presentationForm"><xsl:value-of select="."/></field>
            </xsl:for-each>


            <xsl:for-each select="gmd:graphicOverview/gmd:MD_BrowseGraphic/
                                  gmd:fileName/gco:CharacterString[. != '']">
              <field name="overviewUrl"><xsl:value-of select="."/></field>
            </xsl:for-each>

            <xsl:for-each select="gmd:language/gco:CharacterString|gmd:language/gmd:LanguageCode/@codeListValue">
              <field name="resourceLanguage"><xsl:value-of select="."/></field>
            </xsl:for-each>



            <!-- TODO: create specific INSPIRE template or mode -->
            <!-- INSPIRE themes

            Select the first thesaurus title because some records
            may contains many even if invalid.

            Also get the first title at it may happen that a record
            have more than one.

            Select any thesaurus having the title containing "INSPIRE themes".
            Some records have "GEMET-INSPIRE themes" eg. sk:ee041534-b8f3-4683-b9dd-9544111a0712
            Some other "GEMET - INSPIRE themes"
            -->
            <xsl:for-each
                    select="gmd:descriptiveKeywords
                       /gmd:MD_Keywords[contains(
                         gmd:thesaurusName[1]/gmd:CI_Citation/
                           gmd:title[1]/gco:CharacterString/text(),
                           'INSPIRE themes')]
                      /gmd:keyword/gco:CharacterString">

              <xsl:variable name="inspireTheme" as="xs:string"
                            select="solr:analyzeField('inspireTheme_syn', text())"/>

              <field name="inspireTheme_syn"><xsl:value-of select="text()"/></field>
              <field name="inspireTheme"><xsl:value-of select="$inspireTheme"/></field>

              <!--
              WARNING: Here we only index the first keyword in order
              to properly compute one INSPIRE annex.
              -->
              <xsl:if test="position() = 1">
                <field name="inspireAnnex">
                  <xsl:value-of select="solr:analyzeField('inspireAnnex_syn', $inspireTheme)"/>
                </field>
              </xsl:if>
            </xsl:for-each>

            <field name="numberOfInspireTheme"><xsl:value-of select="count(gmd:descriptiveKeywords
                       /gmd:MD_Keywords[contains(
                         gmd:thesaurusName[1]/gmd:CI_Citation/
                           gmd:title[1]/gco:CharacterString/text(),
                           'GEMET - INSPIRE themes')]
                      /gmd:keyword)"/></field>

            <xsl:for-each
                    select="gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString">
              <!-- TODO: Add geotag on place keyword -->
              <field name="tag"><xsl:value-of select="text()"/></field>
            </xsl:for-each>

            <xsl:for-each select="gmd:topicCategory/gmd:MD_TopicCategoryCode">
              <field name="topic"><xsl:value-of select="."/></field>
              <!-- TODO: Get translation ? -->
            </xsl:for-each>



            <xsl:for-each select="gmd:spatialResolution/gmd:MD_Resolution">
              <xsl:for-each select="gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer[. != '']">
                <field name="resolutionScaleDenominator"><xsl:value-of select="."/></field>
              </xsl:for-each>

              <xsl:for-each select="gmd:distance/gco:Distance[. != '']">
                <field name="resolutionDistance"><xsl:value-of select="concat(., @uom)"/></field>
              </xsl:for-each>
            </xsl:for-each>

            <xsl:for-each select="gmd:spatialRepresentationType/gmd:MD_SpatialRepresentationTypeCode/@codeListValue[. != '']">
              <field name="spatialRepresentationType"><xsl:value-of select="."/></field>
            </xsl:for-each>



            <xsl:for-each select="gmd:resourceConstraints">
              <xsl:for-each select="*/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue[. != '']">
                <field name="accessConstraints"><xsl:value-of select="."/></field>
              </xsl:for-each>
              <xsl:for-each select="*/gmd:otherConstraints/gco:CharacterString[. != '']">
                <field name="otherConstraints"><xsl:value-of select="."/></field>
              </xsl:for-each>
              <xsl:for-each select="*/gmd:classification/gmd:MD_ClassificationCode/@codeListValue[. != '']">
                <field name="constraintClassification"><xsl:value-of select="."/></field>
              </xsl:for-each>
              <xsl:for-each select="*/gmd:useLimitation/gco:CharacterString[. != '']">
                <field name="useLimitation"><xsl:value-of select="."/></field>
              </xsl:for-each>
            </xsl:for-each>




            <xsl:for-each select="*/gmd:EX_Extent">

              <xsl:for-each select="gmd:geographicElement/gmd:EX_GeographicDescription/
                gmd:geographicIdentifier/gmd:MD_Identifier/
                gmd:code/gco:CharacterString[normalize-space(.) != '']">
                <field name="geoTag"><xsl:value-of select="."/></field>
              </xsl:for-each>

              <!-- TODO: index bbox -->
            </xsl:for-each>


            <!-- Service information -->
            <xsl:for-each select="srv:serviceType/gco:LocalName">
              <field name="serviceType"><xsl:value-of select="text()"/></field>
              <xsl:variable name="inspireServiceType" as="xs:string"
                            select="solr:analyzeField(
                            'inspireServiceType', text(),
                            'query',
                            'org.apache.lucene.analysis.miscellaneous.KeepWordFilter',
                            0)"/>
              <xsl:if test="$inspireServiceType != ''">
                <field name="inspireServiceType"><xsl:value-of select="lower-case($inspireServiceType)"/></field>
              </xsl:if>
              <xsl:if test="following-sibling::srv:serviceTypeVersion">
                <field name="serviceTypeAndVersion">
                  <xsl:value-of select="concat(
                            text(),
                            $separator,
                            following-sibling::srv:serviceTypeVersion/gco:CharacterString/text())"/>
                </field>
              </xsl:if>
            </xsl:for-each>
          </xsl:for-each>







          <xsl:for-each select="gmd:referenceSystemInfo/gmd:MD_ReferenceSystem">
            <xsl:for-each select="gmd:referenceSystemIdentifier/gmd:RS_Identifier">
              <xsl:variable name="crs" select="gmd:code/gco:CharacterString"/>

              <xsl:if test="$crs != ''">
                <field name="coordinateSystem"><xsl:value-of select="$crs"/></field>
              </xsl:if>
            </xsl:for-each>
          </xsl:for-each>





          <!-- INSPIRE Conformity -->

          <!-- Conformity for data sets -->
          <xsl:choose>
            <xsl:when test="$isDataset">
              <xsl:for-each-group select="gmd:dataQualityInfo/*/gmd:report"
                                  group-by="*/gmd:result/*/gmd:specification/gmd:CI_Citation/
            gmd:title/gco:CharacterString">

                <xsl:variable name="title" select="current-grouping-key()"/>
                <xsl:if test="count($eu10892010/*[lower-case(normalize-space(.)) =
                    lower-case(normalize-space($title))]) = 1">

                  <xsl:variable name="pass" select="*/gmd:result/*/gmd:pass/gco:Boolean"/>
                  <field name="inspireConformResource"><xsl:value-of select="$pass"/></field>
                </xsl:if>
              </xsl:for-each-group>
            </xsl:when>
            <xsl:otherwise>
              <!-- Conformity for services -->
              <xsl:for-each-group select="gmd:dataQualityInfo/*/gmd:report"
                                  group-by="*/gmd:result/*/gmd:specification/gmd:CI_Citation/
            gmd:title/gco:CharacterString">

                <xsl:variable name="title" select="current-grouping-key()"/>
                <xsl:if test="count($eu9762009/*[lower-case(normalize-space(.)) =
                    lower-case(normalize-space($title))]) = 1">

                  <xsl:variable name="pass" select="*/gmd:result/*/gmd:pass/gco:Boolean"/>
                  <field name="inspireConformResource"><xsl:value-of select="$pass"/></field>
                </xsl:if>
              </xsl:for-each-group>
            </xsl:otherwise>
          </xsl:choose>





          <xsl:for-each select="gmd:dataQualityInfo/*/
                                  gmd:lineage/gmd:LI_Lineage/
                                    gmd:statement/gco:CharacterString[. != '']">
            <field name="lineage"><xsl:value-of select="."/></field>
          </xsl:for-each>


          <xsl:for-each select="gmd:distributionInfo/*">
            <xsl:for-each select="gmd:distributionFormat/*/gmd:name/gco:CharacterString">
              <field name="format"><xsl:value-of select="."/></field>
            </xsl:for-each>

            <xsl:for-each select="gmd:transferOptions/*/
                                    gmd:onLine/*[gmd:linkage/gmd:URL != '']">
              <field name="linkUrl"><xsl:value-of select="gmd:linkage/gmd:URL"/></field>
              <!-- TODO add link field to contains URL, name and protocol -->
            </xsl:for-each>
          </xsl:for-each>

          <!-- Service/dataset relation. Create document for the association.
          Note: not used for indicators anymore
           This could be used to retrieve :
          {!child of=documentType:metadata}+documentType:metadata +id:9940c446-6fd4-4ab3-a4de-7d0ee028a8d1
          {!child of=documentType:metadata}+documentType:metadata +resourceType:service +serviceType:view
          {!child of=documentType:metadata}+documentType:metadata +resourceType:service +serviceType:download
           -->
          <xsl:for-each select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:operatesOn">
            <xsl:variable name="associationType" select="'operatesOn'"/>
            <xsl:variable name="serviceType" select="../srv:serviceType/gco:LocalName"/>
            <!--<xsl:variable name="relatedTo" select="@uuidref"/>-->
            <xsl:variable name="getRecordByIdId">
              <xsl:if test="@xlink:href != ''">
                <xsl:analyze-string select="@xlink:href"
                                    regex=".*[i|I][d|D]=([\w\-\.]*).*">
                  <xsl:matching-substring>
                    <xsl:value-of select="regex-group(1)"/>
                  </xsl:matching-substring>
                </xsl:analyze-string>
              </xsl:if>
            </xsl:variable>

            <xsl:variable name="datasetId">
              <xsl:choose>
                <xsl:when test="$getRecordByIdId != ''">
                  <xsl:value-of select="$getRecordByIdId"/>
                </xsl:when>
                <xsl:when test="@uuidref != ''">
                  <xsl:value-of select="@uuidref"/>
                </xsl:when>
              </xsl:choose>
            </xsl:variable>

            <xsl:if test="$datasetId != ''">
              <field name="recordOperateOn"><xsl:value-of select="$datasetId"/></field>

              <doc>
                <field name="id"><xsl:value-of
                        select="concat('association', $identifier,
                  $associationType, $datasetId)"/></field>
                <field name="documentType">association</field>
                <field name="record"><xsl:value-of select="$identifier"/></field>
                <field name="associationType"><xsl:value-of select="$associationType"/></field>
                <field name="relatedTo"><xsl:value-of select="$datasetId"/></field>
              </doc>
              <doc>
                <field name="id"><xsl:value-of
                        select="concat('association',
                  $associationType, $datasetId)"/></field>
                <field name="documentType">association2</field>
                <field name="record"><xsl:value-of select="$identifier"/></field>
                <field name="associationType"><xsl:value-of select="concat($associationType, $serviceType)"/></field>
                <field name="relatedTo"><xsl:value-of select="$datasetId"/></field>
              </doc>
            </xsl:if>
          </xsl:for-each>
        </doc>


        <!-- Create or update child document and register service relation
        in recordOperatedBy field and als associated resources.

         TODO: Some countries are using uuidref to store
         resource identifier and not metadata identifier. -->
        <xsl:for-each select="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:operatesOn">
          <!--<xsl:message>## child record <xsl:value-of select="@xlink:href"/> </xsl:message>-->

          <!--
          uuiref store resource identifier and not metadata identifier.
              <xsl:variable name="relatedTo" select="@uuidref"/>
          -->
          <xsl:variable name="relatedTo">
            <xsl:if test="@xlink:href != ''">
              <xsl:analyze-string select="@xlink:href"
                                  regex=".*id=([\w-]*).*">
                <xsl:matching-substring>
                  <xsl:value-of select="regex-group(1)"/>
                </xsl:matching-substring>
              </xsl:analyze-string>
            </xsl:if>
          </xsl:variable>
          <!--<xsl:message>## child record <xsl:value-of select="$relatedTo"/> </xsl:message>-->

          <xsl:choose>
            <xsl:when test="$relatedTo">
              <doc>
                <field name="id"><xsl:value-of select="$relatedTo"/></field>
                <field name="metadataIdentifier" update="set"><xsl:value-of select="$relatedTo"/></field>
                <field name="recordOperatedBy" update="add"><xsl:value-of select="$identifier"/></field>
                <xsl:for-each select="../srv:serviceType/gco:LocalName">
                  <field name="recordOperatedByType" update="add"><xsl:value-of select="."/></field>
                </xsl:for-each>
              </doc>
            </xsl:when>
            <xsl:otherwise>
              <xsl:message>Failed to extract metadata identifier from @uuidref or xlink:href <xsl:value-of select="@xlink:href"/></xsl:message>
            </xsl:otherwise>
          </xsl:choose>

        </xsl:for-each>
      </xsl:for-each>
    </add>
  </xsl:template>


  <xsl:template mode="index-contact" match="*">
    <xsl:param name="fieldSuffix" select="''" as="xs:string"/>

    <!-- Select the first child which should be a CI_ResponsibleParty.
    Some records contains more than one CI_ResponsibleParty which is
    not valid and they will be ignored. -->
    <xsl:variable name="organisationName"
                  select="*[1]/gmd:organisationName/(gco:CharacterString|gmx:Anchor)"
                  as="xs:string*"/>

    <xsl:variable name="role"
                  select="*[1]/gmd:role/*/@codeListValue"
                  as="xs:string?"/>
    <xsl:if test="normalize-space($organisationName) != ''">
      <field name="Org{$fieldSuffix}"><xsl:value-of select="$organisationName"/></field>
      <field name="{$role}Org{$fieldSuffix}"><xsl:value-of select="$organisationName"/></field>
    </xsl:if>
    <field name="contact{$fieldSuffix}">{
      org:"<xsl:value-of select="replace($organisationName, '&quot;', '\\&quot;')"/>",
      role:"<xsl:value-of select="$role"/>"
      }
    </field>
  </xsl:template>
</xsl:stylesheet>