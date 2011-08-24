package eu.nets.javazone.service;

import eu.nets.javazone.route.PaymentRoute;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("upload/")
@Transactional
public class UploadFileController {

    @Autowired
    private ProducerTemplate producerTemplate;



    @RequestMapping(value = "okfile", method = RequestMethod.GET)
    public String handleOkFileUpload() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("okfil.txt");
        String contents = IOUtils.toString(inputStream);
        producerTemplate.sendBody(PaymentRoute.RECEIVE, contents);
        return "redirect:/admin/";
    }

    @RequestMapping(value = "badfile", method = RequestMethod.GET)
    public String handleBadFileUpload() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("badfil.txt");
        String contents = IOUtils.toString(inputStream);
        producerTemplate.sendBody(PaymentRoute.RECEIVE, contents);
        return "redirect:/admin/";
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String testService() {
        System.out.println("Get called on upload service.....");
        return "Hello world!";
    }
}
