package eu.nets.camel.component.batch;


import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("batchStore")
public class JdbcBatchStore implements BatchStore {

    private static final String DATABASE_PREFIX = "PUBLIC.";

    private final SimpleJdbcTemplate jdbcTemplate;
    private final Dialect dialect = new HSQLDialect();

    private final boolean deleteRowsOnConsume = true;
    private PlatformTransactionManager transactionManager;

    private int batchSize = 10;

    @Autowired
    public JdbcBatchStore(final DataSource dataSource, final PlatformTransactionManager transactionManager) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
        this.transactionManager = transactionManager;
    }

    @Override
    public void put(final Object reference, final String batchKey) {

        System.err.println("PUTTING '" + reference + "'.");

        jdbcTemplate.update("insert into " + DATABASE_PREFIX  + "batch (reference, batchkey, status, version) values (?,?,0,0)", reference, batchKey);
    }

    @Override
    public List<String> take(final String batchKey) {
        return takeReferences(batchKey);
    }

    private List<String> takeReferences(String batchKey) {

        String sql = "select * from " + DATABASE_PREFIX + "batch where status = 0 and batchkey = '" + batchKey + "'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(dialect.getLimitString(sql, 0, 1), batchSize);
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> references = new ArrayList<String>(list.size());
        List<Object[]> batchArgs = new ArrayList<Object[]>(list.size());
        for (Map<String, Object> rowMap : list) {
            long version = Long.parseLong("" + rowMap.get("VERSION"));
            long nextVersion = version + 1;
            String reference = (String) rowMap.get("REFERENCE");
            references.add(reference);
            if (deleteRowsOnConsume) {
                batchArgs.add(new Object[]{reference, version});
            } else {
                batchArgs.add(new Object[]{nextVersion, reference, version});
            }
        }
        if (deleteRowsOnConsume) {
            jdbcTemplate.batchUpdate("delete from " + DATABASE_PREFIX  + "batch where status = 0 and reference = ? and version = ?", batchArgs);
        } else {
            jdbcTemplate.batchUpdate("update " + DATABASE_PREFIX  + "batch set status = 1, version = ? where status = 0 and reference = ? and version = ?", batchArgs);
        }

        return references;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }
}
