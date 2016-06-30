package cz.geek.gooddata.shell.commands;

import com.gooddata.model.ModelDiff;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Component
public class ModelCommand extends AbstractGoodDataCommand {

    @Autowired
    public ModelCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"model"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }


    @CliCommand(value = "model", help = "Get project model diff in MAQL format")
    public String maql(@CliOption(key = {"diff"}, mandatory = true, help = "Model file in json format") File model) throws FileNotFoundException {
        final ModelDiff diff = getGoodData().getModelService().getProjectModelDiff(getCurrentProject(), new FileReader(model)).get();
        return StringUtils.join(diff.getUpdateMaql(), "\n");
    }

}
