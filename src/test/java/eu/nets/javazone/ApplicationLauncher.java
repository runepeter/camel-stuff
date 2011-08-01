package eu.nets.javazone;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationLauncher {

    // the only way to catch and throw exceptions during startup is to intercept
    // the call chain and collect them:
    protected final transient List<Exception> startupExceptions = Collections.synchronizedList(new ArrayList<Exception>());

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        ApplicationLauncher launcher = new ApplicationLauncher();
        launcher.startServer(9090);
    }

    public Server startServer(int port) {
        Server server = new Server();
        Connector defaultConnector = new SocketConnector();
        defaultConnector.setPort(port);
        server.setConnectors(new Connector[] { defaultConnector });
        WebAppContext webContext = createWebappContextHandler();
        server.addHandler(webContext);
        try {
            startupExceptions.clear();
            server.start();

            if (startupExceptions.size() > 0) {
                Exception exception = startupExceptions.get(0);
                throw new RuntimeException("Errors during startup. The first is " + exception.getClass().getName() + ": " + exception.getMessage(), exception);
            }
            System.out.println("Server started on http://" + server.getConnectors()[0].getName());
        } catch (Exception e) {
            System.exit(-1);
        }
        // NB virker ikke
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(webContext.getServletContext());
        return server;
    }

    private WebAppContext createWebappContextHandler() {
        WebAppContext context = new WebAppContext("src/main/webapp", "/") {
            @Override
            protected void startContext() throws Exception {
                try {
                    super.startContext();
                } catch (Exception e) {
                    addStartupException(e);
                    throw e;
                }
            }
        };
        return context;
    }

    private void addStartupException(Exception e) {
        startupExceptions.add(e);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
