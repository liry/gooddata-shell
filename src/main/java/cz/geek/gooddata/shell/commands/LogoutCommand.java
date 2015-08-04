package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.project.Project;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import jline.console.ConsoleReader;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 */
@Component
public class LogoutCommand extends AbstractGoodDataCommand {

    @Autowired
    public LogoutCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"logout"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }

    @CliCommand(value = "logout", help = "Logout GoodData platform")
    public String login() {
        holder.logout();
        return "Logged out.";
    }
}
