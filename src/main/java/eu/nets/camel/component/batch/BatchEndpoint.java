package eu.nets.camel.component.batch;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultPollingConsumerPollStrategy;
import org.apache.log4j.Logger;

import java.util.List;

public class BatchEndpoint extends DefaultEndpoint {

    private final Logger logger = Logger.getLogger(BatchEndpoint.class);

    private final String batchKey;
    private final BatchStore batchStore;

    private boolean stopOnEmpty = false;

    public BatchEndpoint(String endpointUri, Component component, String batchKey, BatchStore batchStore) {
        super(endpointUri, component);
        this.batchKey = batchKey;
        this.batchStore = batchStore;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new BatchProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {

        BatchConsumer consumer = new BatchConsumer(this, processor);
        if (stopOnEmpty) {
            consumer.setPollStrategy(new StopOnEmptyPollingConsumerPollStrategy());
        }

        return consumer;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    String getBatchKey() {
        return batchKey;
    }

    BatchStore getBatchStore() {
        return batchStore;
    }

    public void setMaxMessagesPerPoll(int maxMessagesPerPoll) {
        batchStore.setBatchSize(maxMessagesPerPoll);
    }

    public void setStopOnEmpty(final boolean stopOnEmpty) {
        this.stopOnEmpty = stopOnEmpty;
    }

    private class StopOnEmptyPollingConsumerPollStrategy extends DefaultPollingConsumerPollStrategy {
        @Override
        public void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
            if (polledMessages == 0) {

                CamelContext context = endpoint.getCamelContext();
                List<Route> routes = context.getRoutes();
                for (Route route : routes) {
                    if (route.getConsumer() == consumer) {
                        try {
                            logger.info("No more messages to batch up for endpoint " + endpoint + "... Stopping Route.");
                            context.stopRoute(route.getId());
                            return;
                        } catch (Exception e) {
                            throw new RuntimeException("Unable to stop BatchConsumer for endpoint " + endpoint + ".", e);
                        }
                    }
                }

                throw new IllegalStateException("Unable to stop route on empty poll result. Active Route not found.");
            }
        }
    }
}
