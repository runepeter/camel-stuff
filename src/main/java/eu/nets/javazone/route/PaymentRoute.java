package eu.nets.javazone.route;

import eu.nets.javazone.service.BalanceValidator;
import eu.nets.javazone.service.CSMInsert;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentRoute extends RouteBuilder {

    public static final String ENDPOINT_CLEARING = "direct:clearing";
    public static final String ENDPOINT_CLEARING_AGGREGATOR = "jms:clearingsystem";
    public static final String ENDPOINT_BALANCE = "jms:balance";
    public static final String ENDPOINT_RECEIPT = "direct:receipt";
    public static final String ENDPOINT_RECEIVE = "seda:receive";
    public static final String ENDPOINT_FILINSERT = "direct:filinsert";
    public static final String WEB_RECEIVE = "direct:webreceive";

    private final AtomicLong startTime = new AtomicLong(0);

    @Override
    public void configure() throws Exception {

        from(WEB_RECEIVE)
                .routeId("webreceive")
                .setHeader("CamelFileName", simple("${in.body.originalFilename}"))
                .transform(simple("${in.body.inputStream}")).to(ENDPOINT_RECEIVE);

        from(ENDPOINT_RECEIVE)
                .routeId("receive")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        startTime.set(System.currentTimeMillis());
                    }
                })
                .transacted()
                .inOnly(ENDPOINT_RECEIPT)
                .split(body().tokenize("\n")).setHeader("JallaCorrelationId", constant("RUNE"))
                .to(ENDPOINT_BALANCE + "?transferExchange=true");

        from(ENDPOINT_RECEIPT).routeId("receipt").log("receipt called");
        from(ENDPOINT_FILINSERT).beanRef("fileReceiver");

        from(ENDPOINT_BALANCE + "?concurrentConsumers=100&maxConcurrentConsumers=100&transacted=true").transacted().routeId("balance")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.err.println(new Date() + " :: " + Thread.currentThread().getName());
                    }
                })
                .delay(1500)
                .setHeader("BALANCE_CHECK")
                .constant("OK")
                .log("balance called")
                .beanRef("balanceService")
                .validate(bean(BalanceValidator.class))
                .to(ENDPOINT_CLEARING_AGGREGATOR + "?transferExchange=true");

        from(ENDPOINT_CLEARING_AGGREGATOR + "?concurrentConsumers=100&maxConcurrentConsumers=100&transacted=true").filter(header("BALANCE_CHECK").isEqualTo("OK"))
                .aggregate(header("JallaCorrelationId"), groupExchanges())
                .aggregationRepositoryRef("aggregatorRepository")
                .completionSize(1000)
                .completionTimeout(30000)
                .discardOnCompletionTimeout()

                .onCompletion().process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {

                if (exchange.getProperty("CamelAggregatedCompletedBy").equals("timeout")) {
                    System.err.println("TIMEOUT, BABY!!");
                }

            }
        }).end()
     //   .transform(property("CamelGroupedExchange"))
                .to(ENDPOINT_CLEARING)
                ;

        from(ENDPOINT_CLEARING).routeId("clearing")
                .beanRef("csminsert")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.err.println(exchange.getIn().getBody());
                    }
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        System.err.println("Took " + (System.currentTimeMillis() - startTime.get()) / 1000 + " secs.");
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
