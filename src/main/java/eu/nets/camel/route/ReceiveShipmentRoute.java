package eu.nets.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReceiveShipmentRoute extends SpringRouteBuilder
{
    private static final String KVITTERING = "direct:kvittering";
    private static final String KVITTERING_OUT = "file:{{receipt.dir}}";
    private static final String VALIDATED = "file:validated/";

    @Override
    public void configure() throws Exception
    {
        from("file:{{nfs.dir}}/inbound").to("file:{{local.dir}}/inbound");

        from("file:{{local.dir}}/inbound")
                .beanRef("shipmentValidator", "validate")
                .multicast()
                    .to(KVITTERING)
                    .filter(header("validation-status").isEqualTo(200)).to(VALIDATED)
                .end();

        from(KVITTERING).beanRef("receiptTransformer").to(KVITTERING_OUT);


    }
}
