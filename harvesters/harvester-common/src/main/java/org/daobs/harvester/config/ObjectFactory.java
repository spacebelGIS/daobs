/**
 * Copyright 2014-2016 European Environment Agency
 *
 * Licensed under the EUPL, Version 1.1 or – as soon
 * they will be approved by the European Commission -
 * subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package org.daobs.harvester.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.daobs.harvester.config package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

  private final static QName _Territory_QNAME = new QName("http://daobs.org", "territory");
  private final static QName _Folder_QNAME = new QName("http://daobs.org", "folder");
  private final static QName _Name_QNAME = new QName("http://daobs.org", "name");
  private final static QName _NbOfRecordsPerPage_QNAME = new QName("http://daobs.org", "nbOfRecordsPerPage");
  private final static QName _PointOfTruthURLPattern_QNAME = new QName("http://daobs.org", "pointOfTruthURLPattern");
  private final static QName _Uuid_QNAME = new QName("http://daobs.org", "uuid");
  private final static QName _ServiceMetadata_QNAME = new QName("http://daobs.org", "serviceMetadata");
  private final static QName _Filter_QNAME = new QName("http://daobs.org", "filter");
  private final static QName _Url_QNAME = new QName("http://daobs.org", "url");

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.daobs.harvester.config
   *
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link Harvesters }
   *
   */
  public Harvesters createHarvesters() {
    return new Harvesters();
  }

  /**
   * Create an instance of {@link Harvester }
   *
   */
  public Harvester createHarvester() {
    return new Harvester();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "territory")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  public JAXBElement<String> createTerritory(String value) {
    return new JAXBElement<String>(_Territory_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "folder")
  public JAXBElement<String> createFolder(String value) {
    return new JAXBElement<String>(_Folder_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "name")
  public JAXBElement<String> createName(String value) {
    return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "nbOfRecordsPerPage")
  public JAXBElement<Integer> createNbOfRecordsPerPage(Integer value) {
    return new JAXBElement<Integer>(_NbOfRecordsPerPage_QNAME, Integer.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "pointOfTruthURLPattern")
  public JAXBElement<String> createPointOfTruthURLPattern(String value) {
    return new JAXBElement<String>(_PointOfTruthURLPattern_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "uuid")
  public JAXBElement<String> createUuid(String value) {
    return new JAXBElement<String>(_Uuid_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "serviceMetadata")
  public JAXBElement<String> createServiceMetadata(String value) {
    return new JAXBElement<String>(_ServiceMetadata_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "filter")
  public JAXBElement<Object> createFilter(Object value) {
    return new JAXBElement<Object>(_Filter_QNAME, Object.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   *
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "url")
  public JAXBElement<String> createUrl(String value) {
    return new JAXBElement<String>(_Url_QNAME, String.class, null, value);
  }

}
