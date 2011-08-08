package eu.nets.javazone.route;

import eu.nets.javazone.service.CSMInsert;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;

public class PaymentRoute extends RouteBuilder {

    public static final String ENDPOINT_CLEARING_ROUTING = "direct:clearing";
    public static final String ENDPOINT_CLEARING = "direct:clearingsystem";
    public static final String ENDPOINT_BALANCE = "direct:balance";
    public static final String ENDPOINT_RECEIPT = "direct:receipt";
    public static final String ENDPOINT_RECEIVE = "direct:receive";

    @Override
    public void configure() throws Exception {
        from(ENDPOINT_RECEIVE)
                .routeId("receive")
                .transacted()
                .inOnly(ENDPOINT_RECEIPT)
                .beanRef("fileReceiver")
                .split(body(String.class).tokenize("\n"))
                //.shareUnitOfWork()
               // .parallelProcessing().threads(10)
                .to(ENDPOINT_BALANCE);




        from(ENDPOINT_RECEIPT).routeId("receipt").log("receipt called");

        from(ENDPOINT_BALANCE).routeId("balance").delay(1500).setHeader("BALANCE_CHECK").constant("OK").log("balance called").to(ENDPOINT_CLEARING_ROUTING);

        from(ENDPOINT_CLEARING_ROUTING).filter(header("BALANCE_CHECK").isEqualTo("OK"))
                .aggregate(property("CamelCorrelationId"), groupExchanges()).completionTimeout(30000).completionSize(property("CamelSplitSize")).to(ENDPOINT_CLEARING_ROUTING);

        from(ENDPOINT_CLEARING).routeId("clearing").log("clearing called").bean(CSMInsert.class);

    }

    public static AggregationStrategy groupExchanges() {
        return new GroupedExchangeAggregationStrategy() {
            @Override
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                Exchange aggregate = super.aggregate(oldExchange, newExchange);
                System.err.println(Thread.currentThread() + " -> " + aggregate.hashCode());
                aggregate.setProperty("CamelSplitSize", newExchange.getProperty("CamelSplitSize"));
                return aggregate;
            }
        };
    }

}
