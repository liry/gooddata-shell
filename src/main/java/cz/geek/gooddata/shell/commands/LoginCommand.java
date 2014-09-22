package cz.geek.gooddata.shell.commands;

import com.gooddata.GoodDataException;
import com.gooddata.project.Project;
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
        try {
            final String current = holder.getGoodData().getAccountService().getCurrent().getId();
            if (projectId != null) {
                final Project project = holder.getGoodData().getProjectService().getProjectById(projectId);
                holder.setCurrentProject(project);
            }
            return "Logged " + current;
        } catch (GoodDataException e) {
            return "Unable to log in: " + e.getMessage();
        }
    }
}
