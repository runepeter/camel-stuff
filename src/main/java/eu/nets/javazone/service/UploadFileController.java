package eu.nets.javazone.service;

import com.sun.org.apache.bcel.internal.util.ClassPath;
import eu.nets.javazone.route.PaymentRoute;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("upload/")
@Transactional
public class UploadFileController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @RequestMapping(method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        String contents = IOUtils.toString(file.getInputStream());
        if (contents == null) {
            System.out.println("No contents found");
            return "redirect:/status/";
        }
        InputStream inputStream = IOUtils.toInputStream(contents);
        producerTemplate.sendBody(PaymentRoute.WEB_RECEIVE, file);
        return "redirect:/admin/";
    }

    @RequestMapping(value = "okfile", method = RequestMethod.GET)
    public String handleOkFileUpload() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("okfil.txt");
        String contents = IOUtils.toString(inputStream);
        producerTemplate.sendBodyAndHeader(PaymentRoute.ENDPOINT_RECEIVE, contents, "CamelFileName", "okfil.txt");
        return "redirect:/admin/";
    }

     @RequestMapping(value = "badfile", method = RequestMethod.GET)
    public String handleBadFileUpload() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("badfil.txt");
        String contents = IOUtils.toString(inputStream);
        producerTemplate.sendBodyAndHeader(PaymentRoute.ENDPOINT_RECEIVE, contents, "CamelFileName", "badfil.txt");
        return "redirect:/admin/";
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String testService() {
        System.out.println("Get called on upload service.....");
        return "Hello world!";
    }
}
