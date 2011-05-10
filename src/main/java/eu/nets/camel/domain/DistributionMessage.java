package eu.nets.camel.domain;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;

import javax.persistence.*;
import java.io.ByteArrayInputStream;

@Entity
@Table(name = "message")
public class DistributionMessage
{
    /**
     *
     */
    private static final long serialVersionUID = 8496087166198616020L;

    public static final Namespace NAMESPACE = new Namespace("ns", "http://www.nets.eu/nets-share/shipment/1.0");


    private long id;
    private Document xml;

    public DistributionMessage()
    {
    }

    public DistributionMessage(final Document xml)
    {
        this.xml = xml;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    @Lob
    @Column(name = "xml", nullable = false, updatable = true)
    protected byte[] getXml()
    {
        return xml.asXML().getBytes();
    }

    protected void setXml(byte[] xml)
    {
        try
        {
            SAXReader reader = new SAXReader();
            this.xml = reader.read(new ByteArrayInputStream(xml));
        } catch (DocumentException e)
        {
            throw new RuntimeException("Unable to set XML from supplied string.", e);
        }
    }

}
