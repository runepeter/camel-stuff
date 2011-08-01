package eu.nets.javazone.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/jmxrest")
public class Jmx2RestController {
    private static Logger log= Logger.getLogger(Jmx2RestController.class);

    private MBeanServer jmxServer;

    private Map<String, ObjectName> routeMap;

    public Jmx2RestController() throws Exception {
        jmxServer = ManagementFactory.getPlatformMBeanServer();

    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String getAttributeForRoute(@RequestParam("route") String route, @RequestParam("attribute") String attribute) throws Exception {

        populateRouteIds();
        ObjectName objectName = routeMap.get(route);
        if (objectName==null){
            log.warn("Can't find mbean for route " +route );
            return "error";
        }
        String result = String.valueOf(jmxServer.getAttribute(objectName, attribute));
        return result;
    }

    private void populateRouteIds() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException,
            InstanceNotFoundException, ReflectionException {
        if (routeMap != null) {
            return;
        }
        routeMap = new HashMap<String, ObjectName>();
        ObjectName searchObject = new ObjectName("org.apache.camel:type=routes,*");
        Set<ObjectName> routes = jmxServer.queryNames(searchObject, null);
        for (ObjectName routeObjectName : routes) {
            String routeId = (String) jmxServer.getAttribute(routeObjectName, "RouteId");
            routeMap.put(routeId, routeObjectName);
        }
        log.info("Routes" + routeMap);
    }

}
