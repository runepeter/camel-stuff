package eu.nets.camel.mottak.receipt;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class ReceiptTransformer
{
    public void transform(Exchange exchange)
    {
        int statusCode = exchange.getIn().getHeader("validation-status", Integer.class);
        if (statusCode == 200)
        {
            exchange.getIn().setBody("The shipment passed validation.");
        } else
        {
            exchange.getIn().setBody("The shipment DID NOT pass validation.");
        }
    }
}
