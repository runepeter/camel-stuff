package eu.nets.javazone.service;

import eu.nets.javazone.domain.Fil;
import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Component
public class FileReceiver {

    @Autowired
    private SessionFactory sessionFactory;

    public void insert(InputStream inputStream, String fileName) {
        Fil interchange = parseFrom(inputStream, fileName);
        sessionFactory.getCurrentSession().saveOrUpdate(interchange);
    }

    public void insert(MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String originalFileName = file.getName();
    }

    private Fil parseFrom(InputStream inputStream, String originalFileName) {
        Fil fil = new Fil();
        fil.setOriginalFilename(originalFileName);
        try {
            fil.setContent(IOUtils.toString(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //  fil.setReceivedDateTime(new Date());
        return fil;
    }

     private void insert(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String originalFileName = file.getName();
        insert(inputStream, originalFileName);
    }
}
