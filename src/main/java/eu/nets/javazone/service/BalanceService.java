package eu.nets.javazone.service;

import com.sun.jmx.snmp.tasks.ThreadService;
import org.apache.camel.Exchange;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;

@Service
public class BalanceService {

    private final SimpleJdbcTemplate jdbc;

    @Autowired
    public BalanceService(final DataSource dataSource) {
        this.jdbc = new SimpleJdbcTemplate(dataSource);
    }


    /**
     * This method sleeps for 1,5 seconds to simulate a remote call to a legacy system or something.
     */
    public void checkBalanceAndReserveAmount(Exchange exchange) {
        String body = exchange.getIn().getBody(String.class);
        doLegacyStuff();

        int amount = Integer.parseInt(body.split(";")[2].trim());

        int available = jdbc.queryForInt("select saldo from balance where id = 1");
        int reserved = jdbc.queryForInt("select sum(saldo) from reserved");

        if (amount > (available-reserved)) {
            exchange.getIn().setHeader("BALANCE_CHECK", "NOT_OK");
        } else {
            exchange.getIn().setHeader("BALANCE_CHECK", "OK");
            jdbc.update("insert into reserved(saldo, status, created) values(?, 0, ?)", amount, new Date());
        }

    }

    public void commitReservation(Exchange exchange) {
        int count = jdbc.update("update reserved set status=1 where status=0");
        System.err.println("Updated " + count + " reservations.");
    }

    public void rollbackReservations() {

        Date now = new Date();
        Date date = DateUtils.addSeconds(now, -30);

        int count = jdbc.update("delete from reserved where status=0 and created < ?", date);
        System.err.println("Rolled back " + count + " reservations.");
    }

    private void doLegacyStuff() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
