package eu.nets.javazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class BalanceService {

    private final SimpleJdbcTemplate jdbc;

    @Autowired
    public BalanceService(final DataSource dataSource) {
        this.jdbc = new SimpleJdbcTemplate(dataSource);
    }

    public void logBalanceCall(final String transaction) {
        jdbc.update("INSERT INTO BALANCE(ID, TRANSACTION) VALUES (balance_seq.nextval, ?)", transaction);
    }

}
