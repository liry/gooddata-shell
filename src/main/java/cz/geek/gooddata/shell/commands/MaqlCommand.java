package cz.geek.gooddata.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class MaqlCommand extends GoodDataCommand {

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

        holder.getGoodData().getModelService().updateProjectModel(holder.getCurrentProject(), maql).get();
        return "Executed";
    }

}
