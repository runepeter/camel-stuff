package eu.nets.javazone.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component("csminsert")
public class CSMInsert {

    private final SimpleJdbcTemplate jdbc;

    @Autowired
    public CSMInsert(final DataSource dataSource) {
        this.jdbc = new SimpleJdbcTemplate(dataSource);
    }

    public void insert(List<String> payments) {


        int i = 0;
        int saldo = 0;
        saldo = jdbc.queryForInt("select saldo from balance where account = ?", "11111111111");
        for (String payment : payments) {
            int amount = Integer.parseInt(payment.split(";")[2].trim());
            saldo = saldo - amount;
            jdbc.update("update balance set saldo = ? where account = ?", saldo, "11111111111");
            saldo = jdbc.queryForInt("select saldo from balance where account = ?", "11111111111");
            doSomeLegacyCommunication();
        }
        jdbc.update("update reserved set saldo=0 where account = ? and status=1", "11111111111");
    }


    private void doSomeLegacyCommunication() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
