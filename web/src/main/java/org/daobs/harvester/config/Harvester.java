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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element ref="{http://daobs.org}uuid"/>
 *         &lt;element ref="{http://daobs.org}territory"/>
 *         &lt;element ref="{http://daobs.org}folder"/>
 *         &lt;element ref="{http://daobs.org}name"/>
 *         &lt;element ref="{http://daobs.org}url"/>
 *         &lt;element ref="{http://daobs.org}filter" minOccurs="0"/>
 *         &lt;element ref="{http://daobs.org}nbOfRecordsPerPage" minOccurs="0"/>
 *         &lt;element ref="{http://daobs.org}pointOfTruthURLPattern"/>
 *         &lt;element ref="{http://daobs.org}serviceMetadata"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
  "uuid",
  "territory",
  "folder",
  "name",
  "url",
  "filter",
  "nbOfRecordsPerPage",
  "pointOfTruthURLPattern",
  "serviceMetadata"
})
@XmlRootElement(name = "harvester", namespace = "http://daobs.org")
public class Harvester implements Serializable {

  private static final long serialVersionUID = 7526471155622776147L;

  @XmlElement(namespace = "http://daobs.org", required = true)
  protected String uuid;
  @XmlElement(namespace = "http://daobs.org", required = true)
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlSchemaType(name = "NCName")
  protected String territory;
  @XmlElement(namespace = "http://daobs.org", required = true)
  protected String folder;
  @XmlElement(namespace = "http://daobs.org", required = true)
  protected String name;
  @XmlElement(namespace = "http://daobs.org", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String url;
  @XmlElement(namespace = "http://daobs.org")
  protected Object filter;
  @XmlElement(namespace = "http://daobs.org")
  protected Integer nbOfRecordsPerPage;
  @XmlElement(namespace = "http://daobs.org", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String pointOfTruthURLPattern;
  @XmlElement(namespace = "http://daobs.org", required = true)
  @XmlSchemaType(name = "anyURI")
  protected String serviceMetadata;

  /**
   * Gets the value of the uuid property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Sets the value of the uuid property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setUuid(String value) {
    this.uuid = value;
  }

  /**
   * Gets the value of the territory property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getTerritory() {
    return territory;
  }

  /**
   * Sets the value of the territory property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setTerritory(String value) {
    this.territory = value;
  }

  /**
   * Gets the value of the folder property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getFolder() {
    return folder;
  }

  /**
   * Sets the value of the folder property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setFolder(String value) {
    this.folder = value;
  }

  /**
   * Gets the value of the name property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the url property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the value of the url property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setUrl(String value) {
    this.url = value;
  }

  /**
   * Gets the value of the filter property.
   *
   * @return
   *     possible object is
   *     {@link Object }
   *
   */
  public Object getFilter() {
    return filter;
  }

  /**
   * Sets the value of the filter property.
   *
   * @param value
   *     allowed object is
   *     {@link Object }
   *
   */
  public void setFilter(Object value) {
    this.filter = value;
  }

  /**
   *
   *               If not provided, the harvester config parameter is used.
   *
   *
   * @return
   *     possible object is
   *     {@link Integer }
   *
   */
  public Integer getNbOfRecordsPerPage() {
    return nbOfRecordsPerPage;
  }

  /**
   * Sets the value of the nbOfRecordsPerPage property.
   *
   * @param value
   *     allowed object is
   *     {@link Integer }
   *
   */
  public void setNbOfRecordsPerPage(Integer value) {
    this.nbOfRecordsPerPage = value;
  }

  /**
   * Gets the value of the pointOfTruthURLPattern property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getPointOfTruthURLPattern() {
    return pointOfTruthURLPattern;
  }

  /**
   * Sets the value of the pointOfTruthURLPattern property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setPointOfTruthURLPattern(String value) {
    this.pointOfTruthURLPattern = value;
  }

  /**
   * Gets the value of the serviceMetadata property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getServiceMetadata() {
    return serviceMetadata;
  }

  /**
   * Sets the value of the serviceMetadata property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setServiceMetadata(String value) {
    this.serviceMetadata = value;
  }

}
