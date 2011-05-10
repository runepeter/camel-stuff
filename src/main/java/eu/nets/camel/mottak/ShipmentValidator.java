package eu.nets.camel.mottak;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

@Component
public class ShipmentValidator
{
    private final Schema schema;
    
    public ShipmentValidator()
    {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        StreamSource source = new StreamSource(ShipmentValidator.class.getResourceAsStream("/xsd/shipment.xsd"));
        try {
            this.schema = factory.newSchema(source);
        } catch (SAXException e) {
            throw new IllegalArgumentException("Unable to open and parse xsd shipment.xsd from classpath.", e);
        }
    }

    public void validate(@Body File shipmentFile, Exchange exchange) {

        Source xml = new StreamSource(shipmentFile);

        Validator validator = schema.newValidator();
        try
        {
            validator.validate(xml);
            setStatus(200, exchange);
        } catch (SAXException e)
        {
            setStatus(400, exchange);
        } catch (IOException e)
        {
            throw new RuntimeException("Unable to validate shipment file " + shipmentFile + ".", e);
        }

    }

    private void setStatus(int statusCode, Exchange exchange) {
        exchange.getIn().setHeader("validation-status", statusCode);
    }

}
