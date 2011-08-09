package eu.nets.javazone.transactions;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;



public class TransactionBatchComponentTest extends CamelSpringTestSupport {

    @EndpointInject(uri = "mock:endpoint")
    private MockEndpoint mockEndpoint;


    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(new String[]{"classpath:META-INF/spring/applicationContext.xml", "classpath:META-INF/spring/spring-datasource.xml", "classpath:META-INF/spring/spring-datasource-tx.xml"});
    }



    @Test
    @Ignore //denne testen tar lang tid..
    public void testTransactionRollbackBatchComponent() throws Exception {
//        BatchStore batchStore = applicationContext.getBean(JdbcBatchStore.class);
//
//        template.sendBody("seda:one", "fail");
//        Thread.sleep(1000);
//        List<String> refs = batchStore.take("xxx");
//        // since the batch consumer is rolled back we expect the batchStore to still have the message
//        assertEquals(1, refs.size());
//
//        List<String> refs2 = batchStore.take("yyy");
//        // since the batch consumer is rolled back we expect the one reference inserted be rolled back,
//        // and thus 0 messages with yyy as batch key
//        assertEquals(0, refs2.size());
//
//        mockEndpoint.expectedMessageCount(1);
//        mockEndpoint.expectedBodyReceived().constant(asList("testreference"));
//        template.sendBody("seda:one", "testreference");
//        Thread.sleep(1000);
//        List<String> refs3 = batchStore.take("xxx");
//        // this message is not rolled back and thus consumed, therefore we expect 0 messages
//        assertEquals(0, refs3.size());
//
//        List<String> refs4 = batchStore.take("yyy");
//        // the route completes and we expect one message in batch store yyy to be inserted
//        assertEquals(1, refs4.size());
//
//        // and then to receive the message in the end.
//        mockEndpoint.assertIsSatisfied();
//
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        RouteBuilder builder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("seda:one")
                        .transacted()
                        .to("batch:xxx");
                from("batch:xxx?maxMessagesPerPoll=5")
                        .transacted()
                        .to("direct:two");

                from("direct:two")
                        .to("batch:yyy")
                        .to("direct:three");

                from("direct:three")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                List<String> refs = exchange.getIn().getBody(List.class);
                                for (int i = 0; i < refs.size(); i++) {
                                    String s = refs.get(i);
                                    if ("fail".equals(s)) {
                                        throw new RuntimeException("should fail!");
                                    }
                                }

                            }
                        })
                        .to("seda:four");

                from("seda:four").to(mockEndpoint);
            }
        };


        return builder;
    }


}
