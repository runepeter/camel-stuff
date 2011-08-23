package eu.nets.javazone.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("message/")
public class MessageResource {

    public static String message = "";


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String takeMessage() {
        String s = message;
        message = "";
        return s;
    }
}
