package eu.nets.camel.component.batch;


import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

public interface BatchStore {
    void put(Object reference, String batchKey);
    List<String> take(String batchKey);
    int getBatchSize();
    void setBatchSize(int batchSize);
    PlatformTransactionManager getTransactionManager();
}
