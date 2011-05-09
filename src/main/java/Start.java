import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Properties;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO evaluate:
 * http://simplericity.org/svn/simplericity/trunk/jetty-console/jetty
 * -console-core/src/main/java/JettyConsoleBootstrapMainClass.java
 * 
 * (will enable us to put jetty deps in separate directory)
 * 
 */
public class Start {

    private static final Logger logger = LoggerFactory.getLogger(Start.class);

    private static final String WEBAPPLICATION_CONTEXT_NAME = "/";

    private static final File  PROPERTIES_FILE = new File("environment.properties");

    public static void main(String[] args) throws Exception {
        Properties properties = loadProperties();

        Server server = new Server();

        Connector defaultConnector = new SocketConnector();
        defaultConnector.setPort(Integer.parseInt((String) properties.get("server.port")));
        server.setConnectors(new Connector[] { defaultConnector });
        server.addHandler(createWebappContextHandler());
        try {
            server.start();
            System.out.println("Server started on http://" + server.getConnectors()[0].getName());
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    private static Handler createWebappContextHandler() {
        WebAppContext context = new WebAppContext();
        context.setContextPath(WEBAPPLICATION_CONTEXT_NAME);

        File file = new File("etc/");
        logger.info("Adding additional classpath folder: '" + file.getAbsoluteFile() + "'.");

        context.setExtraClasspath(file.getAbsolutePath());
        ProtectionDomain protectionDomain = Start.class.getProtectionDomain();
        setWebappFilesLocation(context, protectionDomain);
        return context;
    }

    private static void setWebappFilesLocation(WebAppContext context, ProtectionDomain protectionDomain) {
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());
    }

    private static Properties loadProperties() throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        if (PROPERTIES_FILE.exists()) {
            System.out.println("Loading environment from " + PROPERTIES_FILE.getAbsolutePath());
            properties.load(new FileInputStream(PROPERTIES_FILE));
        } else {
            System.err.println("Could not load environment file. Please enter properties manually");
            System.out.println();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            readProperty("jdbc.url", properties, reader);
            readProperty("jdbc.username", properties, reader);
            readProperty("jdbc.password", properties, reader);
            readProperty("server.port", properties, reader);
            properties.store(new FileOutputStream(PROPERTIES_FILE), null);
            reader.close();
            System.out.println("Properties written to " + PROPERTIES_FILE.getAbsoluteFile());
        }
        return properties;
    }

    private static void readProperty(String property, Properties properties, BufferedReader reader) throws IOException {
        System.out.print(property + ": ");
        properties.put(property, reader.readLine());
    }
}
