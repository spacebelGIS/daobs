
package org.daobs.indicator.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{http://daobs.org}variables"/>
 *         &lt;element ref="{http://daobs.org}indicators"/>
 *       &lt;/sequence>
 *       &lt;attribute name="computationTime" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "variables",
    "indicators"
})
@XmlRootElement(name = "reporting", namespace = "http://daobs.org")
public class Reporting {

    @XmlElement(namespace = "http://daobs.org", required = true)
    protected Variables variables;
    @XmlElement(namespace = "http://daobs.org", required = true)
    protected Indicators indicators;
    @XmlAttribute(name = "computationTime")
    protected Integer computationTime;

    /**
     * Gets the value of the variables property.
     * 
     * @return
     *     possible object is
     *     {@link Variables }
     *     
     */
    public Variables getVariables() {
        return variables;
    }

    /**
     * Sets the value of the variables property.
     * 
     * @param value
     *     allowed object is
     *     {@link Variables }
     *     
     */
    public void setVariables(Variables value) {
        this.variables = value;
    }

    /**
     * Gets the value of the indicators property.
     * 
     * @return
     *     possible object is
     *     {@link Indicators }
     *     
     */
    public Indicators getIndicators() {
        return indicators;
    }

    /**
     * Sets the value of the indicators property.
     * 
     * @param value
     *     allowed object is
     *     {@link Indicators }
     *     
     */
    public void setIndicators(Indicators value) {
        this.indicators = value;
    }

    /**
     * Gets the value of the computationTime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getComputationTime() {
        return computationTime;
    }

    /**
     * Sets the value of the computationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setComputationTime(Integer value) {
        this.computationTime = value;
    }

}
