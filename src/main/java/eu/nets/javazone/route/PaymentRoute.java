package eu.nets.javazone.route;

import eu.nets.javazone.service.BalanceValidator;
import eu.nets.javazone.service.CSMInsert;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;

public class PaymentRoute extends RouteBuilder {

    public static final String ENDPOINT_CLEARING = "direct:clearing";
    public static final String ENDPOINT_CLEARING_AGGREGATOR = "direct:clearingsystem";
    public static final String ENDPOINT_BALANCE = "direct:balance";
    public static final String ENDPOINT_RECEIPT = "direct:receipt";
    public static final String ENDPOINT_RECEIVE = "seda:receive";
    public static final String ENDPOINT_FILINSERT = "direct:filinsert";
    public static final String WEB_RECEIVE = "direct:webreceive";

    @Override
    public void configure() throws Exception {

        from(WEB_RECEIVE)
                .routeId("webreceive")
                .setHeader("CamelFileName", simple("${in.body.originalFilename}"))
                .transform(simple("${in.body.inputStream}")).to(ENDPOINT_RECEIVE);

        from(ENDPOINT_RECEIVE)
                .routeId("receive")
                .transacted()
                .inOnly(ENDPOINT_RECEIPT)
                        // .inOnly(ENDPOINT_FILINSERT)
                .split(body().tokenize("\n"))
                .to(ENDPOINT_BALANCE)
                .to(ENDPOINT_CLEARING_AGGREGATOR);




        from(ENDPOINT_RECEIPT).routeId("receipt").log("receipt called");
        from(ENDPOINT_FILINSERT).beanRef("fileReceiver");

        from(ENDPOINT_BALANCE).routeId("balance")
                .delay(1500)
                .validate(bean(BalanceValidator.class))
                .setHeader("BALANCE_CHECK")
                .constant("OK")
                .log("balance called")
        ;

        from(ENDPOINT_CLEARING_AGGREGATOR).filter(header("BALANCE_CHECK").isEqualTo("OK"))
                .aggregate(property("CamelCorrelationId"), groupExchanges()).completionTimeout(10000).completionSize(property("CamelSplitSize")).transform(property("CamelGroupedExchange")).to(ENDPOINT_CLEARING);

        from(ENDPOINT_CLEARING).routeId("clearing")
                .transacted()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        System.err.println(exchange.getIn().getBody());
                    }
                })
                .log("clearing called").beanRef("csminsert");



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
