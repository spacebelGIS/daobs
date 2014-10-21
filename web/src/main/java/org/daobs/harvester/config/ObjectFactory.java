
package org.daobs.harvester.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.daobs.harvester.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Territory_QNAME = new QName("http://daobs.org", "territory");
    private final static QName _Folder_QNAME = new QName("http://daobs.org", "folder");
    private final static QName _Name_QNAME = new QName("http://daobs.org", "name");
    private final static QName _Filter_QNAME = new QName("http://daobs.org", "filter");
    private final static QName _Url_QNAME = new QName("http://daobs.org", "url");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.daobs.harvester.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Harvesters }
     * 
     */
    public Harvesters createHarvesters() {
        return new Harvesters();
    }

    /**
     * Create an instance of {@link Harvester }
     * 
     */
    public Harvester createHarvester() {
        return new Harvester();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://daobs.org", name = "territory")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createTerritory(String value) {
        return new JAXBElement<String>(_Territory_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://daobs.org", name = "folder")
    public JAXBElement<String> createFolder(String value) {
        return new JAXBElement<String>(_Folder_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://daobs.org", name = "name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://daobs.org", name = "filter")
    public JAXBElement<Object> createFilter(Object value) {
        return new JAXBElement<Object>(_Filter_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://daobs.org", name = "url")
    public JAXBElement<String> createUrl(String value) {
        return new JAXBElement<String>(_Url_QNAME, String.class, null, value);
    }

}
