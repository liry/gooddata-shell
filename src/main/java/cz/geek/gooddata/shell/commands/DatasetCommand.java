package cz.geek.gooddata.shell.commands;

import com.gooddata.dataset.Dataset;
import com.gooddata.dataset.DatasetManifest;
import com.gooddata.dataset.DatasetService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.supercsv.io.CsvListReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.gooddata.dataset.DatasetManifest.Part;
import static java.util.Arrays.asList;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

/**
 */
@Component
public class DatasetCommand extends AbstractGoodDataCommand {

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
            @CliOption(key = {"csv"}, mandatory = true, help = "CSV file") File csv,
            @CliOption(key = {"mode"}, mandatory = false, help = "Upload mode") UploadMode mode,
            @CliOption(key = {"populates"}, mandatory = false, help = "TODO") String populates
    ) throws IOException {

        if (mode == null) {
            mode = UploadMode.FULL;
        }
        final DatasetService service = getGoodData().getDatasetService();
        final DatasetManifest manifest = service.getDatasetManifest(getCurrentProject(), dataset);

        final String[] header;
        try (final CsvListReader reader = new CsvListReader(new InputStreamReader(new FileInputStream(csv)), STANDARD_PREFERENCE)) {
            header = reader.getHeader(true);
        }
        if (populates != null) {
            final String[] pop = populates.split(",");
            if (pop.length != header.length) {
                throw new IllegalArgumentException("CSV header items " + header.length + " != populates items " + pop.length);
            }
            final HashMap<String, Part> parts = new HashMap<>(manifest.getParts().size());
            for (Part part: manifest.getParts()) {
                for (String p: part.getPopulates()) {
                    parts.put(p, part);
                }
            }
            if (header.length != parts.size()) {
                throw new IllegalArgumentException("CSV header items " + header.length + " != dataset populates " + parts.size());
            }
            // todo this should be part of Manifest class
            for (int i = 0; i<header.length; i++) {
                final Part part = parts.get(pop[i]);
                if (part == null) {
                    throw new IllegalArgumentException("Unknown part for populate " + pop[i]);
                }
                part.setColumnName(header[i]);
                part.setUploadMode(mode.name());
            }
        } else {
            final HashSet<String> columns = new HashSet<>(asList(header));
            for (Part part: manifest.getParts()) {
                if (part.getPopulates() == null || part.getPopulates().size() != 1) {
                    throw new IllegalStateException("Only parts with exactly one populates are supported " + part.getPopulates());
                }
                final String field = part.getPopulates().iterator().next();
                final String[] items = StringUtils.delimitedListToStringArray(field, ".");
                final String column = items == null ? field : items[items.length - 1];
                if (!columns.contains(column)) {
                    throw new IllegalStateException("Unable to automap field " + field +
                            ". CSV file contains columns " + columns + " but not the given column " + column);
                }
                part.setColumnName(column);
                part.setUploadMode(mode.name());
            }
        }
        try (final FileInputStream stream = new FileInputStream(csv)) {
            service.loadDataset(getCurrentProject(), manifest, stream).get();
        }
        return "Loaded dataset: " + dataset;
    }

    @CliCommand(value = "dataset list", help = "List datasets")
    public String list() {
        return print(getGoodData().getDatasetService().listDatasets(getCurrentProject()),
                asList("URI", "Identifier", "Title"), new RowExtractor<Dataset>() {
            @Override
            public List<?> extract(Dataset dataset) {
                return asList(dataset.getLink(), dataset.getIdentifier(), dataset.getTitle());
            }
        });
    }

    public static enum UploadMode {
        INCREMENTAL, FULL
    }
}
