package eu.nets.javazone;

import org.apache.camel.builder.RouteBuilder;

public class PaymentRoute extends RouteBuilder {

    public static final String ENDPOINT_CLEARING = "direct:clearing";
    public static final String ENDPOINT_BALANCE = "direct:balance";
    public static final String ENDPOINT_RECEIPT = "direct:receipt";
    public static final String ENDPOINT_RECEIVE = "file:data/receive";

    @Override
    public void configure() throws Exception {


    }
}
