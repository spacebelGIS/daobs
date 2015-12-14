
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
 * 
 * 
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
     * Gets the value of the statsField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatsField() {
        return statsField;
    }

    /**
     * Sets the value of the statsField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatsField(String value) {
        this.statsField = value;
    }

    /**
     * Gets the value of the stats property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStats() {
        return stats;
    }

    /**
     * Sets the value of the stats property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStats(String value) {
        this.stats = value;
    }

}
