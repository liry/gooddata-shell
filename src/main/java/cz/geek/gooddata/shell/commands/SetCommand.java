package cz.geek.gooddata.shell.commands;

import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.OutputFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class SetCommand extends AbstractGoodDataCommand {

    @Autowired
    public SetCommand(GoodDataHolder holder) {
        super(holder);
    }

    @CliCommand(value = "set", help = "Set runtime options")
    public String setOutput(@CliOption(key = "output", mandatory = false, help = "Get or set output format") OutputFormatter formatter) {
        if (formatter != null) {
            holder.setOutputFormatter(formatter);
        }
        return holder.getOutputFormatter().toString();
    }
}
