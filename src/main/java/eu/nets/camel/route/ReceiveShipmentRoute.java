package eu.nets.camel.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReceiveShipmentRoute extends SpringRouteBuilder
{
    private static final String KVITTERING = "direct:kvittering";
    private static final String KVITTERING_OUT = "file:{{receipt.dir}}?fileName=${file:name}_${date:now:yyyyMMddhhmmss}.receipt";
    private static final String VALIDATED = "direct:validated";

    @Override
    public void configure() throws Exception
    {
        from("file:{{nfs.dir}}/inbound").to("file:{{local.dir}}/inbound");

        from("file:{{local.dir}}/inbound?move=processed/")
                .beanRef("shipmentValidator", "validate")
                .multicast()
                    .to(KVITTERING)
                    .filter(header("validation-status").isEqualTo(200)).to(VALIDATED)
                .end();

        from(KVITTERING).beanRef("receiptTransformer").log("${body}").to(KVITTERING_OUT);

        from(VALIDATED)
                .transacted()
                .split(bean("distributionMessageSplitter", "split")).streaming()
                .delay(70)
                .beanRef("distributionMessageRepository", "save");

    }
}
