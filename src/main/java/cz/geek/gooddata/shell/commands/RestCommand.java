package cz.geek.gooddata.shell.commands;

import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class RestCommand extends AbstractGoodDataCommand {

    @Autowired
    public RestCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"rest get"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }

    @CliCommand(value = "rest get", help = "Perform GET request to the given URI")
    public String get(@CliOption(key = {"uri", ""}, mandatory = true) String uri) {
        return getGoodData().getRestService().get(uri);
    }

}
