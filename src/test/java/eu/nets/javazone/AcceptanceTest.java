package eu.nets.javazone;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;

public class AcceptanceTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:clearing")
    private MockEndpoint clearing;

    @EndpointInject(uri = "mock:balance")
    private MockEndpoint balance;

    @EndpointInject(uri = "mock:receipt")
    private MockEndpoint receipt;

    @Test
    public void testReceivePayments() throws Exception {
        balance.expectedMessageCount(5);
        clearing.expectedMessageCount(1);
        receipt.expectedMessageCount(1);

        template.sendBody(PaymentRoute.ENDPOINT_RECEIVE, createPaymentFile(5));

        receipt.assertIsSatisfied();
        balance.assertIsSatisfied();
        clearing.assertIsSatisfied(40000);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        RouteBuilder rb = new PaymentRoute();

        rb.interceptSendToEndpoint(PaymentRoute.ENDPOINT_CLEARING).to(clearing);
        rb.interceptSendToEndpoint(PaymentRoute.ENDPOINT_BALANCE).to(balance);
        rb.interceptSendToEndpoint(PaymentRoute.ENDPOINT_RECEIPT).to(receipt);

        return rb;
    }

    private InputStream createPaymentFile(int numberOfTransactions) {
        StringBuffer transactions = new StringBuffer();
        for(int i = 0; i<numberOfTransactions; i++) {
            transactions.append(generateCreditAccount(i) + ";" + generateDebetAccount(i) + ";" + generateAmount(i) + "\n");
        }
        return IOUtils.toInputStream(transactions);
    }

    private String generateAmount(int i) {
        return ""+i;
    }

    private String generateDebetAccount(int i) {
        long seed = 11111111111L+i;
        return Long.toString(seed).substring(0,11);
    }

    private String generateCreditAccount(int i) {
        long seed = 22222222222L+i;
        return Long.toString(seed).substring(0,11);
    }

}
