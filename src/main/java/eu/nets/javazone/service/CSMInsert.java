package eu.nets.javazone.service;


import eu.nets.javazone.domain.Transaction;
import org.apache.camel.Exchange;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component("csminsert")
public class CSMInsert {

    @Autowired
    private SessionFactory sessionFactory;

    private final SimpleJdbcTemplate jdbc;

    @Autowired
    public CSMInsert(final DataSource dataSource) {
        this.jdbc = new SimpleJdbcTemplate(dataSource);
    }


    public void insert(List<Exchange> exchanges) {
        for (Exchange exchange : exchanges) {

            int amount = Integer.parseInt(exchange.getIn().getBody(String.class).split(";")[2].trim());
            int saldo = jdbc.queryForInt("select saldo from balance where id = 1");


            saldo = saldo - amount;
            jdbc.update("update balance set saldo =?", saldo);
            Transaction transaction = Transaction.parse(exchange.getIn().getBody(String.class));
            sessionFactory.getCurrentSession().saveOrUpdate(transaction);
        }
    }

}
