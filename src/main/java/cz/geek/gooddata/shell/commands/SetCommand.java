package cz.geek.gooddata.shell.commands;

import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.OutputFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class SetCommand extends AbstractGoodDataCommand {

    @Autowired
    public SetCommand(GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"set output", "set exception"})
    public boolean isAvailable() {
        return true;
    }

    @CliCommand(value = "set output", help = "Set runtime options")
    public String setOutput(@CliOption(key = {"", "format"}, mandatory = false, help = "Get or set output format") OutputFormatter formatter) {
        if (formatter != null) {
            holder.setOutputFormatter(formatter);
        }
        return "Output format set to: '" + holder.getOutputFormatter().toString() + "'";
    }

    @CliCommand(value = "set exception", help = "Set exception logging / show the last exception")
    public String setException(@CliOption(key = {"", "format"}, mandatory = false, help = "Get or set exception") ExceptionFormat format) {
        if (format != null) {
            switch (format) {
                case message:
                    holder.setPrintStackTrace(false);
                    break;
                case full:
                    holder.setPrintStackTrace(true);
                    break;
                case last:
                    if (holder.getLastException() != null) {
                        holder.getLastException().printStackTrace(System.err);
                        return "";
                    }
            }

        }
        return "Exception format set to: '" + (holder.isPrintStackTrace() ? ExceptionFormat.full : ExceptionFormat.message) + "'";
    }

    static enum ExceptionFormat {
        message, full, last
    }
}
