package eu.nets.javazone.route;

import eu.nets.javazone.service.MessageResource;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentRoute extends RouteBuilder {

    public static final String RECEIVE = "jms:receive";
    public static final String RECEIPT = "jms:receipt";
    public static final String BALANCE = "jms:balance";
    public static final String CLEARING = "direct:clearing";

    private final AtomicLong startTime = new AtomicLong(0);

    @Override
    public void configure() throws Exception {

        from(RECEIVE)
                .routeId("receive")
                .process(new StartTimingProcessor())
                ;

        from(BALANCE)
                .routeId("balance")
                .transacted()
                .beanRef("balanceService", "checkBalanceAndReserveAmount")
                .validate(header("BALANCE_CHECK").isEqualTo("OK"));

        from(RECEIPT)
                .routeId("receipt")
                .transform(body().prepend("Received OK\n"))
                .to("file:data/receipts/");

        from(CLEARING)
                .routeId("clearing")
                .transacted()
                .beanRef("csminsert")
                .process(new StopTimingProcessor());
    }



















    private Predicate timeout() {
        return header("CamelAggregatedCompletedBy").isEqualTo("timeout");
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

    class StartTimingProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            startTime.set(System.currentTimeMillis());
        }
    }

    class StopTimingProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            MessageResource.message = "Last shipment cleared in " + (System.currentTimeMillis() - startTime.get()) / 1000 + " secs in total.";
            System.err.println("Last shipment cleared in " + (System.currentTimeMillis() - startTime.get()) / 1000 + " secs in total.");
        }
    }
}