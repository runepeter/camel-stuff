package eu.nets.javazone.service;

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

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String testService() {
        System.out.println("Get called on upload service.....");
        return "Hello world!";
    }
}
