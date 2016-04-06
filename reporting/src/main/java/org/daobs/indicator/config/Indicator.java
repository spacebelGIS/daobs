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
 *         &lt;element ref="{http://daobs.org}name"/>
 *         &lt;element ref="{http://daobs.org}expression"/>
 *         &lt;element ref="{http://daobs.org}parameters"/>
 *         &lt;element ref="{http://daobs.org}format" minOccurs="0"/>
 *         &lt;element ref="{http://daobs.org}value" minOccurs="0"/>
 *         &lt;element ref="{http://daobs.org}status" minOccurs="0"/>
 *         &lt;element ref="{http://daobs.org}comment" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="numberFormat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="error" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
  "name",
  "expression",
  "parameters",
  "format",
  "value",
  "status",
  "comment"
})
@XmlRootElement(name = "indicator", namespace = "http://daobs.org")
public class Indicator {

  @XmlElement(namespace = "http://daobs.org", required = true)
  protected Name name;
  @XmlElement(namespace = "http://daobs.org", required = true)
  protected String expression;
  @XmlElement(namespace = "http://daobs.org", required = true)
  protected Parameters parameters;
  @XmlElement(namespace = "http://daobs.org")
  protected String format;
  @XmlElement(namespace = "http://daobs.org")
  protected String value;
  @XmlElement(namespace = "http://daobs.org")
  protected String status;
  @XmlElement(namespace = "http://daobs.org")
  protected String comment;
  @XmlAttribute(name = "id", required = true)
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "NCName")
  protected String id;
  @XmlAttribute(name = "numberFormat")
  protected String numberFormat;
  @XmlAttribute(name = "error")
  protected Boolean error;

  /**
   * Gets the value of the name property.
   *
   * @return
   *     possible object is
   *     {@link Name }
   *
   */
  public Name getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value
   *     allowed object is
   *     {@link Name }
   *
   */
  public void setName(Name value) {
    this.name = value;
  }

  /**
   * Gets the value of the expression property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getExpression() {
    return expression;
  }

  /**
   * Sets the value of the expression property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setExpression(String value) {
    this.expression = value;
  }

  /**
   * Gets the value of the parameters property.
   *
   * @return
   *     possible object is
   *     {@link Parameters }
   *
   */
  public Parameters getParameters() {
    return parameters;
  }

  /**
   * Sets the value of the parameters property.
   *
   * @param value
   *     allowed object is
   *     {@link Parameters }
   *
   */
  public void setParameters(Parameters value) {
    this.parameters = value;
  }

  /**
   * Gets the value of the format property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getFormat() {
    return format;
  }

  /**
   * Sets the value of the format property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setFormat(String value) {
    this.format = value;
  }

  /**
   * Gets the value of the value property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of the value property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the value of the status property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the value of the status property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setStatus(String value) {
    this.status = value;
  }

  /**
   * Gets the value of the comment property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the value of the comment property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setComment(String value) {
    this.comment = value;
  }

  /**
   * Gets the value of the id property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setId(String value) {
    this.id = value;
  }

  /**
   * Gets the value of the numberFormat property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getNumberFormat() {
    return numberFormat;
  }

  /**
   * Sets the value of the numberFormat property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setNumberFormat(String value) {
    this.numberFormat = value;
  }

  /**
   * Gets the value of the error property.
   *
   * @return
   *     possible object is
   *     {@link Boolean }
   *
   */
  public Boolean isError() {
    return error;
  }

  /**
   * Sets the value of the error property.
   *
   * @param value
   *     allowed object is
   *     {@link Boolean }
   *
   */
  public void setError(Boolean value) {
    this.error = value;
  }

}
