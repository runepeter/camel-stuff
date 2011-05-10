package eu.nets.camel.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReceiveShipmentRoute extends SpringRouteBuilder
{
    @Override
    public void configure() throws Exception
    {
        from("file:{{nfs.dir}}/inbound").to("file:{{local.dir}}/inbound");
    }
}
