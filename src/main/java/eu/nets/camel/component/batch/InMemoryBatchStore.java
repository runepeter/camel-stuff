package eu.nets.camel.component.batch;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.util.*;

public class InMemoryBatchStore implements BatchStore {

    private Map<String, Set<String>> map = new HashMap<String, Set<String>>();

    private int batchSize = 1;

    @Override
    public void put(Object reference, String batchKey) {

        Set<String> set = map.get(batchKey);
        if (set == null) {
            map.put(batchKey, set = new HashSet<String>());
        }

        set.add((String) reference);
    }

    @Override
    public List<String> take(String batchKey) {

        Set<String> set = map.remove(batchKey);
        if (set == null) {
            return new ArrayList<String>();
        } else {

            ArrayList<String> list = new ArrayList<String>(batchSize);
            Iterator<String> iterator = set.iterator();
            for (int i=0;i<Math.min(set.size(), batchSize);i++) {
                list.add(iterator.next());
            }

            return list;
        }

    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return new PlatformTransactionManager() {
            @Override
            public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
                return new DefaultTransactionStatus(null, true, true, false, false, null);
            }

            @Override
            public void commit(TransactionStatus transactionStatus) throws TransactionException {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void rollback(TransactionStatus transactionStatus) throws TransactionException {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }
}
