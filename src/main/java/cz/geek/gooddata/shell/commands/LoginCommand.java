package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.project.Project;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.components.MyGoodData.Credentials;
import jline.console.ConsoleReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 */
@Component
public class LoginCommand extends AbstractGoodDataCommand {

    private int port = 443;
    private String protocol = "https";

    @Autowired
    public LoginCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliCommand(value = "login", help = "Login to GoodData platform")
    public String login(
            @CliOption(key = {"user"}, mandatory = true, help = "User name") String user,
            @CliOption(key = {"pass"}, help = "Password") String pass,
            @CliOption(key = {"host"}, help = "Platform hostname (domain)") String host,
            @CliOption(key = {"project"}, help = "Current project") String project) throws IOException {

        if (StringUtils.isBlank(pass)) {
            final ConsoleReader cr = new ConsoleReader();
            pass = cr.readLine("Password: ", '*');
        }
        final Account current = holder.login(new Credentials(host, user, pass, port, protocol));
        if (project != null) {
            final Project p = getProject(project);
            holder.setCurrentProject(p);
        }
        return "Logged: " + current.getUri() + " " + current.getFirstName() + " " + current.getLastName();
    }

    protected void setPort(final int port) {
        this.port = port;
    }

    protected void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
}
