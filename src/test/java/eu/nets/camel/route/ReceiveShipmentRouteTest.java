package eu.nets.camel.route;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

@ContextConfiguration(locations = "classpath:META-INF/spring/spring-context.xml")
public class ReceiveShipmentRouteTest extends AbstractJUnit4SpringContextTests
{
    @Value("${nfs.dir}/inbound") private File fromDir;
    @Value("${local.dir}/inbound") private File toDir;

    @Before
    public void cleanDirs() throws Exception {
        if (fromDir.isDirectory()) {
            FileUtils.forceDelete(fromDir.getParentFile());
            assertThat(fromDir.mkdirs()).isTrue();
        }
        if (toDir.isDirectory()) {
            FileUtils.forceDelete(toDir.getParentFile());
            assertThat(toDir.mkdirs()).isTrue();
        }
    }

    @Test
    public void testMovingFileFromNetworkShareToLocal() throws Exception {

        File tmpDir = new File(fromDir, "tmp");
        assertThat(tmpDir.mkdirs()).isTrue();

        File file = new File(tmpDir, "jalla.txt");
        FileUtils.writeStringToFile(file, "This is a test");
        assertThat(file).isFile();

        FileUtils.moveFileToDirectory(file, fromDir, false);

        Thread.sleep(1000);

        assertThat(new File(toDir, file.getName())).isFile();
    }
}
