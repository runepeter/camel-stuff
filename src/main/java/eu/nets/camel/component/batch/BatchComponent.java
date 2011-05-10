package eu.nets.camel.component.batch;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.CamelContextHelper;
import org.apache.log4j.Logger;

public class BatchComponent extends DefaultComponent {

    private final Logger logger = Logger.getLogger(BatchComponent.class);

    public BatchComponent(final CamelContext context) {
        super(context);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        String batchStoreRef = "batchStore"; // todo: optional parameter to tell different name.
        String batchKey = remaining;
        return new BatchEndpoint(uri, this, batchKey, lookupBatchStore(batchStoreRef));
    }

    private BatchStore lookupBatchStore(final String batchStoreRef) {
        BatchStore batchStore = CamelContextHelper.lookup(getCamelContext(), batchStoreRef, BatchStore.class);
        if (batchStore == null) {
            batchStore = new InMemoryBatchStore();
            logger.info("BatchStore not found. Default InMemoryBatchStore used instead.");
        }
        return batchStore;
    }

}
