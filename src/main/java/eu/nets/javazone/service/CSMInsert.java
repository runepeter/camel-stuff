package eu.nets.javazone.service;


import eu.nets.javazone.domain.Transaction;
import org.apache.camel.Exchange;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("csminsert")
public class CSMInsert {

    @Autowired
    private SessionFactory sessionFactory;




    public void insert(List<Exchange> exchanges) {
        for (Exchange exchange : exchanges) {
            Transaction transaction = Transaction.parse(exchange.getIn().getBody(String.class));
            sessionFactory.getCurrentSession().saveOrUpdate(transaction);
        }
    }

}
