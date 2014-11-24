package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.project.Project;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import jline.console.ConsoleReader;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 */
@Component
public class LoginCommand extends AbstractGoodDataCommand {

    @Autowired
    public LoginCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliCommand(value = "login", help = "Login to GoodData platform")
    public String login(
            @CliOption(key = {"user"}, mandatory = true, help = "User name") String user,
            @CliOption(key = {"pass"}, mandatory = false, help = "Password") String pass,
            @CliOption(key = {"host"}, mandatory = false, help = "Host") String host,
            @CliOption(key = {"project"}, mandatory = false, help = "Current project URI") String projectUri) throws IOException {

        if (StringUtils.isBlank(pass)) {
            final ConsoleReader cr = new ConsoleReader();
            pass = cr.readLine("Password: ", '*');
        }
        holder.login(host, user, pass);
        final Account current = getGoodData().getAccountService().getCurrent();
        if (projectUri != null) {
            final Project project = getGoodData().getProjectService().getProjectByUri(projectUri);
            holder.setCurrentProject(project);
        }
        return "Logged: " + current.getUri() + " " + current.getFirstName() + " " + current.getLastName();
    }
}
