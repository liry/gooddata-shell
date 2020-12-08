package cz.geek.gooddata.shell.commands;

import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *
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
    public String maql(@CliOption(key = {"maql", ""}, help = "MAQL DDL") String maql,
                       @CliOption(key = "file", help = "MAQL DDL file (executes each row separately)") File file) {
        if (file != null) {
            try {
                final List<String> maqls = FileUtils.readLines(file, UTF_8);
                if (maqls.size() > 0) {
                    getGoodData().getModelService().updateProjectModel(getCurrentProject(), maqls).get();
                } else {
                    throw new IllegalArgumentException("maql file cannot be empty");
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(format("file %s doesn't exist", file.getAbsolutePath()));
            }
        } else if (maql != null && maql.length() > 0) {
            getGoodData().getModelService().updateProjectModel(getCurrentProject(), maql).get();
        } else {
            throw new IllegalArgumentException("maql or file argument has to be specified");
        }
        return "MAQL Executed";
    }

}
