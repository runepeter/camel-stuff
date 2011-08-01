package eu.nets.javazone.service;


import eu.nets.javazone.domain.Transaction;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSMInsert {

      @Autowired
    private SessionFactory sessionFactory;


    public void insert(MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String originalFileName = file.getName();
        insert(inputStream, originalFileName);
    }

    public void insert(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String originalFileName = file.getName();
        insert(inputStream, originalFileName);
    }

    private void insert(InputStream inputStream, String fileName) {
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
