//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.19 at 03:19:34 PM CEST 
//


package net.trevize.labelthem.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *       &lt;choice>
 *         &lt;element ref="{}date"/>
 *         &lt;element ref="{}deleted"/>
 *         &lt;element ref="{}id"/>
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}polygon"/>
 *         &lt;element ref="{}verified"/>
 *         &lt;element ref="{}viewpoint"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "date",
    "deleted",
    "id",
    "name",
    "polygon",
    "verified",
    "viewpoint"
})
@XmlRootElement(name = "object")
public class Object {

    protected Date date;
    protected Deleted deleted;
    protected Id id;
    protected Name name;
    protected Polygon polygon;
    protected Verified verified;
    protected Viewpoint viewpoint;

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    public void setDate(Date value) {
        this.date = value;
    }

    /**
     * Gets the value of the deleted property.
     * 
     * @return
     *     possible object is
     *     {@link Deleted }
     *     
     */
    public Deleted getDeleted() {
        return deleted;
    }

    /**
     * Sets the value of the deleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Deleted }
     *     
     */
    public void setDeleted(Deleted value) {
        this.deleted = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Id }
     *     
     */
    public Id getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Id }
     *     
     */
    public void setId(Id value) {
        this.id = value;
    }

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
     * Gets the value of the polygon property.
     * 
     * @return
     *     possible object is
     *     {@link Polygon }
     *     
     */
    public Polygon getPolygon() {
        return polygon;
    }

    /**
     * Sets the value of the polygon property.
     * 
     * @param value
     *     allowed object is
     *     {@link Polygon }
     *     
     */
    public void setPolygon(Polygon value) {
        this.polygon = value;
    }

    /**
     * Gets the value of the verified property.
     * 
     * @return
     *     possible object is
     *     {@link Verified }
     *     
     */
    public Verified getVerified() {
        return verified;
    }

    /**
     * Sets the value of the verified property.
     * 
     * @param value
     *     allowed object is
     *     {@link Verified }
     *     
     */
    public void setVerified(Verified value) {
        this.verified = value;
    }

    /**
     * Gets the value of the viewpoint property.
     * 
     * @return
     *     possible object is
     *     {@link Viewpoint }
     *     
     */
    public Viewpoint getViewpoint() {
        return viewpoint;
    }

    /**
     * Sets the value of the viewpoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link Viewpoint }
     *     
     */
    public void setViewpoint(Viewpoint value) {
        this.viewpoint = value;
    }

}