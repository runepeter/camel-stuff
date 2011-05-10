package eu.nets.camel.mottak.receipt;

import eu.nets.camel.domain.ShipmentReceipt;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;

@Component
public class ReceiptTransformer
{
    public void transform(@Header("CamelFileName") String filename, Exchange exchange)
    {
        ShipmentReceipt receipt;
        int statusCode = exchange.getIn().getHeader("validation-status", Integer.class);
        if (statusCode == 200)
        {
            receipt = new ShipmentReceipt(filename, "The shipment passed validation.");
        } else
        {
            receipt = new ShipmentReceipt(filename, "The shipment DIT NOT pass validation.");
        }
        exchange.getIn().setBody(receipt);
    }
}
