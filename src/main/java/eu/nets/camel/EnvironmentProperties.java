package eu.nets.camel;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Lasting av properties for spring og camel-ruter. Henter data på classpath eller i fil
 * og overskriver med data fra securekatalog 
 */
public class EnvironmentProperties extends Properties {

    private static final String ENVIRONMENT_PROPERTIES = "environment.properties";

    private static final long serialVersionUID = 923791273912739191L;

    private static Logger log = Logger.getLogger(EnvironmentProperties.class);

    public EnvironmentProperties() {
        loadDefaultProperties();
        loadEnvironmentProperties();
        overRideWithSecureProperties();

        System.out.println("Nets Share configuration properties (" + size() + "):");
        for (Object k : keySet()) {
            String key = (String) k;
            if (key.contains("password")) {
                System.out.println(key + "=<SECRET>");
            } else {
                System.out.println(key + "=" + getProperty(key));
            }
        }
    }

    /**
     * Defaultverdier for applikasjonen
     */
    private void loadDefaultProperties() {
        this.setProperty("nfs.dir", "share/");
        this.setProperty("local.dir", "data/");
        this.setProperty("receipt.dir", "receipt/");
    }

    private void loadEnvironmentProperties() {
        File envProperties = getFile(ENVIRONMENT_PROPERTIES);
        if (envProperties.exists()) {
            log.info("Loading properties file from " + envProperties.getAbsolutePath());
            FileInputStream envInputStream = null;
            try {
                envInputStream = new FileInputStream(envProperties);
                this.load(envInputStream);
            } catch (Exception e) {
                throw new RuntimeException("Error loading properties from file " + envProperties.getAbsolutePath());
            } finally {
                IOUtils.closeQuietly(envInputStream);
            }
        } else {
            URL classPathEnvProperties = getClass().getResource("/"+ ENVIRONMENT_PROPERTIES);
            InputStream input = null;
            if (classPathEnvProperties != null) {
                log.info("Loading properties from classpath " + envProperties.getAbsolutePath());
                try {
                    input = getClass().getResourceAsStream("/"+ ENVIRONMENT_PROPERTIES);
                    this.load(input);
                } catch (IOException e) {
                    log.error("Error loading environment.properties from classpath ");
                } finally {
                    IOUtils.closeQuietly(input);
                }
            } else {
                log.warn("Environment.properties not found on file or classpath");
            }
        }

    }

    private File getFile(String fileName) {

        File file;

        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if (url == null) {

            file = new File(fileName);
            if (!file.exists()) {
                file = new File(System.getProperty("user.dir"), ENVIRONMENT_PROPERTIES);
            }

        } else {
            file = new File(url.getFile());
        }
        
        return file;
    }

    private void overRideWithSecureProperties() {
        Properties secureProps= new Properties();
        File secureFile= getFile("secure/secure-environment.properties");
        if (secureFile.exists()){
            FileInputStream input = null;
            try {
                input = new FileInputStream(secureFile);
                secureProps.load(input);
            } catch (Exception e) {
                throw new RuntimeException("Error loading secure properties from file " + secureFile.getAbsolutePath());
            } finally {
                IOUtils.closeQuietly(input);
            }
            Enumeration<String> propertyNames = (Enumeration<String>) secureProps.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String property = propertyNames.nextElement();
                log.info("Setting property "+ property + " from secure properties" );
                this.setProperty(property, secureProps.getProperty(property));
            }
          
        }
        
    }
}
