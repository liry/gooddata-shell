package cz.geek.gooddata.shell.components;

import com.gooddata.account.Account;
import cz.geek.gooddata.shell.AbstractShellIntegrationTest;
import cz.geek.gooddata.shell.components.MyGoodData.Credentials;
import net.jadler.Jadler;
import org.junit.Test;

import static net.jadler.Jadler.onRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoodDataHolderTest extends AbstractShellIntegrationTest {

    @Test
    public void testLogin() {
        final GoodDataHolder holder = new GoodDataHolder();
        final Credentials credentials = mock(Credentials.class);
        when(credentials.getHost()).thenReturn("localhost");
        when(credentials.getUser()).thenReturn("tester");
        when(credentials.getPass()).thenReturn("pass");
        when(credentials.getPort()).thenReturn(Jadler.port());
        when(credentials.getProtocol()).thenReturn("http");
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/account/profile/current")
                .respond()
                .withBody("{\"accountSetting\":{\"login\": \"tester\"}}");

        final Account account = holder.login(credentials);
        assertThat(account, is(notNullValue()));
        assertThat(account.getLogin(), is("tester"));
    }
}