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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="statsField" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="stats">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="min"/>
 *             &lt;enumeration value="max"/>
 *             &lt;enumeration value="count"/>
 *             &lt;enumeration value="countDistinct"/>
 *             &lt;enumeration value="missing"/>
 *             &lt;enumeration value="mean"/>
 *             &lt;enumeration value="sum"/>
 *             &lt;enumeration value="stddev"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
  "value"
})
@XmlRootElement(name = "query", namespace = "http://daobs.org")
public class Query {

  @XmlValue
  protected String value;
  @XmlAttribute(name = "statsField")
  protected String statsField;
  @XmlAttribute(name = "stats")
  protected String stats;

  /**
   * Gets the value of the value property.
   *
   * @return possible object is {@link String }
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of the value property.
   *
   * @param value allowed object is {@link String }
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the value of the statsField property.
   *
   * @return possible object is {@link String }
   */
  public String getStatsField() {
    return statsField;
  }

  /**
   * Sets the value of the statsField property.
   *
   * @param value allowed object is {@link String }
   */
  public void setStatsField(String value) {
    this.statsField = value;
  }

  /**
   * Gets the value of the stats property.
   *
   * @return possible object is {@link String }
   */
  public String getStats() {
    return stats;
  }

  /**
   * Sets the value of the stats property.
   *
   * @param value allowed object is {@link String }
   */
  public void setStats(String value) {
    this.stats = value;
  }

}
