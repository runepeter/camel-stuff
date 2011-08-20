package eu.nets.javazone.integrationtest;


import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class RouteTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Test
    public void testOneFileWithFourLines() throws Exception {
        String body = "hei\n paa \n deg \n bjoern \n ";
        resultEndpoint.expectedMessageCount(4);
        template.sendBody("direct:start", body);
        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .split(body(String.class).tokenize("\n"))
                        .to("mock:result");
            }
        };
    }
}
