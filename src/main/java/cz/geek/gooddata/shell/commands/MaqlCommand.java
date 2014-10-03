package cz.geek.gooddata.shell.commands;

import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class MaqlCommand extends AbstractGoodDataCommand {

    @Autowired
    public MaqlCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"maql"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }


    @CliCommand(value = "maql", help = "Execute MAQL DDL")
    public String maql(@CliOption(key = {"maql", ""}, mandatory = true, help = "MAQL DDL") String maql) {

        getGoodData().getModelService().updateProjectModel(getCurrentProject(), maql).get();
        return "Executed";
    }

}
