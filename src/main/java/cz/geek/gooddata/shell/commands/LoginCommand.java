package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.project.Project;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class LoginCommand extends GoodDataCommand {

    @Autowired
    public LoginCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliCommand(value = "login", help = "Login to GoodData platform")
    public String login(
            @CliOption(key = {"user"}, mandatory = true, help = "User name") String user,
            @CliOption(key = {"pass"}, mandatory = true, help = "Password") String pass,
            @CliOption(key = {"host"}, mandatory = false, help = "Host") String host,
            @CliOption(key = {"project"}, mandatory = false, help = "Current project") String projectId) {

        holder.login(host, user, pass);
        final Account current = holder.getGoodData().getAccountService().getCurrent();
        if (projectId != null) {
            final Project project = holder.getGoodData().getProjectService().getProjectById(projectId);
            holder.setCurrentProject(project);
        }
        return "Logged: " + current.getSelfLink() + " " + current.getFirstName() + " " + current.getLastName();
    }
}
