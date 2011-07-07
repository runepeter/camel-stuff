package eu.nets.javazone;

import org.apache.camel.builder.RouteBuilder;

public class PaymentRoute extends RouteBuilder {

    public static final String ENDPOINT_CLEARING = "direct:clearing";
    public static final String ENDPOINT_BALANCE = "direct:balance";
    public static final String ENDPOINT_RECEIPT = "direct:receipt";
    public static final String ENDPOINT_RECEIVE = "file:data/receive";

    @Override
    public void configure() throws Exception {
        from(ENDPOINT_RECEIVE)
                .to(ENDPOINT_RECEIPT)
                .split(body(String.class).tokenize("\n"))
                .to(ENDPOINT_BALANCE)
                .filter(header("BALANCE_CHECK").isEqualTo("OK"))
                .aggregate(constant("*")).groupExchanges().completionTimeout(200)
                .to(ENDPOINT_CLEARING);


        from(ENDPOINT_BALANCE).setHeader("BALANCE_CHECK").constant("OK").log("balance called");

        from(ENDPOINT_RECEIPT).log("receipt called");

        from(ENDPOINT_CLEARING).log("clearing called");

    }
}
