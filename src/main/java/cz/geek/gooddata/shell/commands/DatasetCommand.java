package cz.geek.gooddata.shell.commands;

import com.gooddata.dataset.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
@Component
public class DatasetCommand extends GoodDataCommand {

    @Autowired
    public DatasetCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"dataset load", "dataset list"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }


    @CliCommand(value = "dataset load", help = "Load dataset")
    public String load(
            @CliOption(key = {"dataset", ""}, mandatory = true, help = "Dataset id") String dataset,
            @CliOption(key = {"csv"}, mandatory = true, help = "CSV file") File csv) throws FileNotFoundException {

        holder.getGoodData().getDatasetService().loadDataset(holder.getCurrentProject(), dataset, new FileInputStream(csv));
        return "Loaded dataset: " + dataset;
    }

    @CliCommand(value = "dataset list", help = "List datasets")
    public String list() {
        final Collection<Dataset> datasets = holder.getGoodData().getDatasetService().listDatasets(holder.getCurrentProject());
        final List<String> result = new ArrayList<String>();
        for (Dataset dataset: datasets) {
            result.add(dataset.getIdentifier() + " " + dataset.getTitle());
        }
        return StringUtils.collectionToDelimitedString(result, "\n");
    }
}
