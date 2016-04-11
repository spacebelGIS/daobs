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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://daobs.org}identification"/>
 *         &lt;element ref="{http://daobs.org}variables"/>
 *         &lt;element ref="{http://daobs.org}indicators"/>
 *       &lt;/sequence>
 *       &lt;attribute name="computationTime" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="dateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="scope" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="scopeId" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
  "identification",
  "variables",
  "indicators"
})
@XmlRootElement(name = "reporting", namespace = "http://daobs.org")
public class Reporting {

  @XmlElement(namespace = "http://daobs.org", required = true)
  protected Identification identification;
  @XmlElement(namespace = "http://daobs.org", required = true)
  protected Variables variables;
  @XmlElement(namespace = "http://daobs.org", required = true)
  protected Indicators indicators;
  @XmlAttribute(name = "computationTime")
  protected Integer computationTime;
  @XmlAttribute(name = "dateTime")
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar dateTime;
  @XmlAttribute(name = "scope")
  protected String scope;
  @XmlAttribute(name = "scopeId")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "NCName")
  protected String scopeId;
  @XmlAttribute(name = "id", required = true)
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "NCName")
  protected String id;

  /**
   * Gets the value of the identification property.
   *
   * @return possible object is {@link Identification }
   */
  public Identification getIdentification() {
    return identification;
  }

  /**
   * Sets the value of the identification property.
   *
   * @param value allowed object is {@link Identification }
   */
  public void setIdentification(Identification value) {
    this.identification = value;
  }

  /**
   * Gets the value of the variables property.
   *
   * @return possible object is {@link Variables }
   */
  public Variables getVariables() {
    return variables;
  }

  /**
   * Sets the value of the variables property.
   *
   * @param value allowed object is {@link Variables }
   */
  public void setVariables(Variables value) {
    this.variables = value;
  }

  /**
   * Gets the value of the indicators property.
   *
   * @return possible object is {@link Indicators }
   */
  public Indicators getIndicators() {
    return indicators;
  }

  /**
   * Sets the value of the indicators property.
   *
   * @param value allowed object is {@link Indicators }
   */
  public void setIndicators(Indicators value) {
    this.indicators = value;
  }

  /**
   * Gets the value of the computationTime property.
   *
   * @return possible object is {@link Integer }
   */
  public Integer getComputationTime() {
    return computationTime;
  }

  /**
   * Sets the value of the computationTime property.
   *
   * @param value allowed object is {@link Integer }
   */
  public void setComputationTime(Integer value) {
    this.computationTime = value;
  }

  /**
   * Gets the value of the dateTime property.
   *
   * @return possible object is {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getDateTime() {
    return dateTime;
  }

  /**
   * Sets the value of the dateTime property.
   *
   * @param value allowed object is {@link XMLGregorianCalendar }
   */
  public void setDateTime(XMLGregorianCalendar value) {
    this.dateTime = value;
  }

  /**
   * Gets the value of the scope property.
   *
   * @return possible object is {@link String }
   */
  public String getScope() {
    return scope;
  }

  /**
   * Sets the value of the scope property.
   *
   * @param value allowed object is {@link String }
   */
  public void setScope(String value) {
    this.scope = value;
  }

  /**
   * Gets the value of the scopeId property.
   *
   * @return possible object is {@link String }
   */
  public String getScopeId() {
    return scopeId;
  }

  /**
   * Sets the value of the scopeId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setScopeId(String value) {
    this.scopeId = value;
  }

  /**
   * Gets the value of the id property.
   *
   * @return possible object is {@link String }
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value allowed object is {@link String }
   */
  public void setId(String value) {
    this.id = value;
  }

}
