package eu.nets.javazone.service;

import org.apache.camel.Exchange;
import org.apache.camel.spi.AggregationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Date;

@Service
public class BalanceService {

    private final boolean test = false;

    private final SimpleJdbcTemplate jdbc;
    private final AggregationRepository aggregationRepository;

    @Autowired
    public BalanceService(final DataSource dataSource, final AggregationRepository aggregationRepository) {
        this.aggregationRepository = aggregationRepository;
        this.jdbc = new SimpleJdbcTemplate(dataSource);
    }


    /**
     * This method sleeps for 1,5 seconds to simulate a remote call to a legacy system or something.
     */
    public void checkBalanceAndReserveAmount(Exchange exchange) {

        String body = exchange.getIn().getBody(String.class);
        String txId = (String) exchange.getIn().getHeader("MyCorrelationId");

        if (test) {
            callTest();
        } else {
            callProd();
        }

        String creditAccount = body.split(";")[0].trim();
        int amount = Integer.parseInt(body.split(";")[2].trim());

        int available = jdbc.queryForInt("select saldo from balance where account = ?", creditAccount);
        int reserved = jdbc.queryForInt("select sum(saldo) from reserved where account = ?", creditAccount);

        if (amount > (available-reserved)) {
            exchange.getIn().setHeader("BALANCE_CHECK", "NOT_OK");
        } else {
            exchange.getIn().setHeader("BALANCE_CHECK", "OK");
            jdbc.update("insert into reserved(tx, account, saldo, status, created) values(?, ?, ?, 1, ?)", txId, creditAccount, amount, new Date());
        }
    }

    public void commitReservations(Exchange exchange) {
        String txId = (String) exchange.getProperty("CamelAggregatedCorrelationKey");
        int count = jdbc.update("update reserved set status=1 where tx = ? and status=0", txId);
        if (count > 0) {
            System.err.println("Updated " + count + " reservations [" + txId + "].");
        }
    }

    public void rollbackReservations(Exchange exchange) {

        aggregationRepository.confirm(exchange.getContext(), exchange.getExchangeId());

        String txId = (String) exchange.getProperty("CamelAggregatedCorrelationKey");

        int count = jdbc.update("delete from reserved where tx = ?", txId);
        if (count > 0) {
            System.err.println("Rolled back " + count + " reservations [" + txId + "].");
        }
        MessageResource.message = "One shipment timed out before all messages passed balance check.";
    }

    private void callTest() {
        //System.err.println("Mock'ed Balance Check.");
    }

    private void callProd() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
