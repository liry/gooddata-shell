package cz.geek.gooddata.shell;

import net.jadler.Jadler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.JLineShellComponent;

/**
 * Parent instantiating the shell inside a test case, so we can execute the command and then perform assertions
 * on the return value CommandResult.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractShellIntegrationTest {

    private static JLineShellComponent shell;
    private static Bootstrap bootstrap;

    @BeforeClass
    public static void startUp() {
        bootstrap = new Bootstrap();
        shell = bootstrap.getJLineShellComponent();
    }

    @AfterClass
    public static void shutdown() {
        shell.stop();
    }

    @Before
    public void setUp() {
        Jadler.initJadler().withDefaultResponseContentType("application/json");
    }

    @After
    public void tearDown() {
        Jadler.closeJadler();
    }

    public static JLineShellComponent getShell() {
        return shell;
    }

    public static ApplicationContext getContext() {
        return bootstrap.getApplicationContext();
    }
}