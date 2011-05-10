package eu.nets.camel.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReceiveShipmentRoute extends SpringRouteBuilder
{
    private static final String KVITTERING = "direct:kvittering";
    private static final String KVITTERING_OUT = "file:{{receipt.dir}}?fileName=${header.filename}_${date:now:yyyyMMddhhmmss}.receipt";
    private static final String VALIDATED = "direct:validated";

    @Override
    public void configure() throws Exception
    {
        from("file:{{nfs.dir}}/inbound").to("file:{{local.dir}}/inbound");

        from("file:{{local.dir}}/inbound?move=processed/")
                .threads(5, 10)
                .transacted()
                .beanRef("shipmentValidator", "validate")
                .multicast()
                    .to(KVITTERING)
                    .filter(header("validation-status").isEqualTo(200)).to(VALIDATED)
                .end();

        from(KVITTERING)
                .transacted()
                .beanRef("receiptTransformer")
                .beanRef("shipmentReceiptRepository", "save")
                .to("batch:kvittering");

        from("batch:kvittering")
                .transacted()
                .split(body())
                .beanRef("shipmentReceiptRepository", "get")
                .setHeader("filename", simple("body.filename"))
                .transform(simple("body.message"))
                .to(KVITTERING_OUT);

        from(VALIDATED)
                .transacted()
                .split(bean("distributionMessageSplitter", "split")).streaming()
                .process(new Processor()
                {
                    @Override
                    public void process(Exchange exchange) throws Exception
                    {
                        System.err.println("Thread: " + Thread.currentThread().getName());
                    }
                })
                .beanRef("distributionMessageRepository", "save");

    }
}
