/**
 * Copyright 2014-2016 European Environment Agency <p> Licensed under the EUPL, Version 1.1 or – as
 * soon they will be approved by the European Commission - subsequent versions of the EUPL (the
 * "Licence"); You may not use this work except in compliance with the Licence. You may obtain a
 * copy of the Licence at: <p> https://joinup.ec.europa.eu/community/eupl/og_page/eupl <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */

package org.daobs.indicator.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
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
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
  "content"
})
@XmlRootElement(name = "title", namespace = "http://daobs.org")
public class Title {

  @XmlValue
  protected String content;
  @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "NCName")
  protected String lang;

  /**
   * Gets the value of the content property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the value of the content property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setContent(String value) {
    this.content = value;
  }

  /**
   * Gets the value of the lang property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getLang() {
    return lang;
  }

  /**
   * Sets the value of the lang property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setLang(String value) {
    this.lang = value;
  }

}
