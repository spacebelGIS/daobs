
package org.daobs.harvester.config;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{http://daobs.org}harvester" maxOccurs="unbounded"/>
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
    "harvester"
})
@XmlRootElement(name = "harvesters", namespace = "http://daobs.org")
public class Harvesters {

    @XmlElement(namespace = "http://daobs.org", required = true)
    protected List<Harvester> harvester;

    /**
     * Gets the value of the harvester property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the harvester property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHarvester().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Harvester }
     * 
     * 
     */
    public List<Harvester> getHarvester() {
        if (harvester == null) {
            harvester = new ArrayList<Harvester>();
        }
        return this.harvester;
    }

}
