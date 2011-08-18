package eu.nets.javazone.route;

import eu.nets.javazone.service.BalanceValidator;
import eu.nets.javazone.service.CSMInsert;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
                .split(body().tokenize("\n")).parallelProcessing().threads(6)
                .to(ENDPOINT_BALANCE)
                .to(ENDPOINT_CLEARING_AGGREGATOR)
        ;

        from(ENDPOINT_RECEIPT).routeId("receipt").log("receipt called");
        from(ENDPOINT_FILINSERT).beanRef("fileReceiver");

        from(ENDPOINT_BALANCE).routeId("balance")
                .delay(1500)
                .setHeader("BALANCE_CHECK")
                .constant("OK")
                .log("balance called")
//                .beanRef("balanceService")
                .validate(bean(BalanceValidator.class))
        ;

        from(ENDPOINT_CLEARING_AGGREGATOR).filter(header("BALANCE_CHECK").isEqualTo("OK"))
                .aggregate(property("CamelCorrelationId"), groupExchanges())
                .aggregationRepositoryRef("aggregatorRepository")
                .completionTimeout(10000)
                .completionSize(property("CamelSplitSize"))
                .transform(property("CamelGroupedExchange"))
                .to(ENDPOINT_CLEARING);

        from(ENDPOINT_CLEARING).routeId("clearing")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.err.println(exchange.getIn().getBody());
                    }
                })
                .log("clearing called");//.beanRef("csminsert");

    }


    public static AggregationStrategy groupExchanges() {
        return new AggregationStrategy() {

            @Override
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

                List list;
                Exchange answer = oldExchange;

                if (oldExchange == null) {
                    answer = new DefaultExchange(newExchange);
                    list = new ArrayList<Exchange>();
                    answer.getIn().setBody(list);
                } else {
                    list = oldExchange.getIn().getBody(List.class);
                }

                if (newExchange != null) {
                    list.add(newExchange.getIn().getBody());
                }
                return answer;

            }
        };
    }

    private static void assertNoError(Exchange newExchange) {
        if (newExchange.getIn().getBody(String.class).contains("ERROR")) {
            throw new RuntimeException();
        }
    }

}
