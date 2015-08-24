package cz.geek.gooddata.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.commands.HelpCommands;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.support.logging.HandlerUtils;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Delegate for the generic help command (mainly for command-line usage)
 */
@Component
public class HelpCommand implements CommandMarker {

    private final Logger logger = HandlerUtils.getLogger(getClass());

    private final HelpCommands help;

    @Autowired
    public HelpCommand(final HelpCommands help) {
        this.help = help;
    }

    @CliCommand(value = "--help", help = "List all commands usage")
    public void help() {
        logger.info("The GoodData Shell allows CLI access to GoodData platform REST API");
        help.obtainHelp(null);
    }
}
