package cz.geek.gooddata.shell.commands;

import cz.geek.gooddata.shell.AbstractShellIntegrationTest;
import net.jadler.Jadler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import static net.jadler.Jadler.onRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginCommandTest extends AbstractShellIntegrationTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
        getContext().getBean(LoginCommand.class).setPort(Jadler.port());
        getContext().getBean(LoginCommand.class).setProtocol("http");
    }

    @Test
    public void testSuccessfulLogin() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/account/profile/current")
                .respond()
                .withBody("{\"accountSetting\":{\"login\": \"tester\", \"firstName\": \"Test\", \"links\": {\"self\": \"tester-link\"}}}");

        final CommandResult commandResult = getShell()
                .executeCommand("login --user tester --pass pass --host localhost");

        assertThat(commandResult, is(notNullValue()));
        assertThat(commandResult.getResult().toString(), is("Logged: tester-link Test null"));
    }
}