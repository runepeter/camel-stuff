package eu.nets.camel.component.batch;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledExecutorService;

public class BatchConsumer extends ScheduledPollConsumer implements org.apache.camel.BatchConsumer {

    private final Logger logger = Logger.getLogger(BatchConsumer.class);

    private final BatchStore batchStore;
    private final String batchKey;
    private TransactionTemplate transactionTemplate;

    public BatchConsumer(final BatchEndpoint endpoint, final Processor processor) {
        super(endpoint, processor);
        this.batchStore = endpoint.getBatchStore();
        this.batchKey = endpoint.getBatchKey();
        this.transactionTemplate = new TransactionTemplate(batchStore.getTransactionManager());
    }

    BatchConsumer(final BatchEndpoint endpoint, final Processor processor, final ScheduledExecutorService executor) {
        super(endpoint, processor, executor);
        this.batchStore = endpoint.getBatchStore();
        this.batchKey = endpoint.getBatchKey();
        this.transactionTemplate = new TransactionTemplate(batchStore.getTransactionManager());
    }

    @Override
    protected int poll() {
        return transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus status) {

                List<String> references = batchStore.take(batchKey);
                if (references.isEmpty()) {
                    return 0;
                }

                Queue<Object> queue = new LinkedList<Object>();
                queue.add(references);

                processBatch(queue);
                logger.info("Done processing batch of '" + references.size() + "' references.");

                return 1;
            }
       });
    }

    @Override
    public void setMaxMessagesPerPoll(int maxMessagesPerPoll) {
        batchStore.setBatchSize(maxMessagesPerPoll);
    }

    @Override
    public int processBatch(Queue<Object> exchanges) {

        Exchange exchange = getEndpoint().createExchange();
        exchange.getIn().setBody(exchanges.poll());

        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            throw new RuntimeException("Error executing task", e);
        }

        return 1;
    }

    @Override
    public boolean isBatchAllowed() {
        return true;
    }
}
