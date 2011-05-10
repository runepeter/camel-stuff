package eu.nets.camel.component.batch;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class BatchProducer extends DefaultProducer {

    private final BatchStore batchStore;
    private final String batchKey;
    private TransactionTemplate transactionTemplate;

    public BatchProducer(final BatchEndpoint endpoint) {
        super(endpoint);
        this.batchStore = endpoint.getBatchStore();
        this.batchKey = endpoint.getBatchKey();
        this.transactionTemplate = new TransactionTemplate(batchStore.getTransactionManager());
    }

    @Override
    public void process(final Exchange exchange) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Object reference = exchange.getIn().getBody();
                batchStore.put(reference, batchKey);
            }
        });
    }

}
