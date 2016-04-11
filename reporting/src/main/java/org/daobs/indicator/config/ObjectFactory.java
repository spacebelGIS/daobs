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

package org.daobs.indicator.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the org.daobs.indicator.config package. <p>An ObjectFactory allows you to
 * programatically construct new instances of the Java representation for XML content. The Java
 * representation of XML content can consist of schema derived interfaces and classes representing
 * the binding of schema type definitions, element declarations and model groups.  Factory methods
 * for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

  private final static QName _Format_QNAME = new QName("http://daobs.org", "format");
  private final static QName _Comment_QNAME = new QName("http://daobs.org", "comment");
  private final static QName _Value_QNAME = new QName("http://daobs.org", "value");
  private final static QName _Url_QNAME = new QName("http://daobs.org", "url");
  private final static QName _Status_QNAME = new QName("http://daobs.org", "status");
  private final static QName _Expression_QNAME = new QName("http://daobs.org", "expression");
  private final static QName _Author_QNAME = new QName("http://daobs.org", "author");

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes
   * for package: org.daobs.indicator.config
   */
  public ObjectFactory() {
  }

  /**
   * Create an instance of {@link Indicator }
   */
  public Indicator createIndicator() {
    return new Indicator();
  }

  /**
   * Create an instance of {@link Name }
   */
  public Name createName() {
    return new Name();
  }

  /**
   * Create an instance of {@link Parameters }
   */
  public Parameters createParameters() {
    return new Parameters();
  }

  /**
   * Create an instance of {@link Parameter }
   */
  public Parameter createParameter() {
    return new Parameter();
  }

  /**
   * Create an instance of {@link Reports }
   */
  public Reports createReports() {
    return new Reports();
  }

  /**
   * Create an instance of {@link Reporting }
   */
  public Reporting createReporting() {
    return new Reporting();
  }

  /**
   * Create an instance of {@link Identification }
   */
  public Identification createIdentification() {
    return new Identification();
  }

  /**
   * Create an instance of {@link Title }
   */
  public Title createTitle() {
    return new Title();
  }

  /**
   * Create an instance of {@link Variables }
   */
  public Variables createVariables() {
    return new Variables();
  }

  /**
   * Create an instance of {@link Variable }
   */
  public Variable createVariable() {
    return new Variable();
  }

  /**
   * Create an instance of {@link Query }
   */
  public Query createQuery() {
    return new Query();
  }

  /**
   * Create an instance of {@link Indicators }
   */
  public Indicators createIndicators() {
    return new Indicators();
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "format")
  public JAXBElement<String> createFormat(String value) {
    return new JAXBElement<String>(_Format_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "comment")
  public JAXBElement<String> createComment(String value) {
    return new JAXBElement<String>(_Comment_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "value")
  public JAXBElement<String> createValue(String value) {
    return new JAXBElement<String>(_Value_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "url")
  public JAXBElement<String> createUrl(String value) {
    return new JAXBElement<String>(_Url_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "status")
  public JAXBElement<String> createStatus(String value) {
    return new JAXBElement<String>(_Status_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "expression")
  public JAXBElement<String> createExpression(String value) {
    return new JAXBElement<String>(_Expression_QNAME, String.class, null, value);
  }

  /**
   * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
   */
  @XmlElementDecl(namespace = "http://daobs.org", name = "author")
  public JAXBElement<String> createAuthor(String value) {
    return new JAXBElement<String>(_Author_QNAME, String.class, null, value);
  }

}
