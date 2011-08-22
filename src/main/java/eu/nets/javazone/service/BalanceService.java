package eu.nets.javazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Service
public class BalanceService {

    private final SimpleJdbcTemplate jdbc;

    @Autowired
    public BalanceService(final DataSource dataSource) {
        this.jdbc = new SimpleJdbcTemplate(dataSource);
    }

    /*
    public void logBalanceCall(final String transaction) {
        jdbc.update("INSERT INTO BALANCE(ID, TRANSACTION) VALUES (balance_seq.nextval, ?)", transaction);
    }
    */

    public void checkBalanceAndReserveAmount(String body) {

        int amount = Integer.parseInt(body.split(";")[2].trim());

        int available = jdbc.queryForInt("select saldo from balance where id = 1");
        if (amount > available) {
            throw new RuntimeException("Not balance");
        }
        jdbc.update("insert into reserved values(?)", amount);
        //available = available - amount;
        //jdbc.update("update balance set available =?", available);

    }

}
