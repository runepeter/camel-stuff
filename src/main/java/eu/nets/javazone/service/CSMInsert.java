package eu.nets.javazone.service;


import eu.nets.javazone.domain.Transaction;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSMInsert {

    @Autowired
    private SessionFactory sessionFactory;




    public void insert(@Body InputStream inputStream, @Header("CamelFileName") String fileName) {
        List<Transaction> transactions = parseFrom(inputStream, fileName);
        for (Transaction transaction : transactions) {
            sessionFactory.getCurrentSession().saveOrUpdate(transaction);
        }
    }

    private List<Transaction> parseFrom(InputStream inputStream, String originalFileName) {
        // todo: faker en trans..

        Transaction transaction = new Transaction();
        transaction.setCreditAccount("123");
        transaction.setDebetAccount("321");
        transaction.setAmount("12");
        transaction.setCreditorName("Bjørn");
        transaction.setCurrency("NOK");
        ArrayList arrayList = new ArrayList();
        arrayList.add(transaction);
        return arrayList;
    }
}
