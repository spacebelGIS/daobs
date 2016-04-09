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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://daobs.org}title"/>
 *         &lt;element ref="{http://daobs.org}author" minOccurs="0"/>
 *         &lt;element ref="{http://daobs.org}url" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
  "title",
  "author",
  "url"
})
@XmlRootElement(name = "identification", namespace = "http://daobs.org")
public class Identification {

  @XmlElement(namespace = "http://daobs.org", required = true)
  protected Title title;
  @XmlElement(namespace = "http://daobs.org")
  protected String author;
  @XmlElement(namespace = "http://daobs.org")
  protected String url;

  /**
   * Gets the value of the title property.
   *
   * @return possible object is {@link Title }
   */
  public Title getTitle() {
    return title;
  }

  /**
   * Sets the value of the title property.
   *
   * @param value allowed object is {@link Title }
   */
  public void setTitle(Title value) {
    this.title = value;
  }

  /**
   * Gets the value of the author property.
   *
   * @return possible object is {@link String }
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Sets the value of the author property.
   *
   * @param value allowed object is {@link String }
   */
  public void setAuthor(String value) {
    this.author = value;
  }

  /**
   * Gets the value of the url property.
   *
   * @return possible object is {@link String }
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the value of the url property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUrl(String value) {
    this.url = value;
  }

}
