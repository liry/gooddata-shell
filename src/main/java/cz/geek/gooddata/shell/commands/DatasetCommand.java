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
import org.supercsv.io.CsvListReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gooddata.dataset.DatasetManifest.Part;
import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.delimitedListToStringArray;
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
            @CliOption(key = {"populates"}, mandatory = false, help = "TODO") String populates,
            @CliOption(key = {"map"}, mandatory = false, help = "TODO") String map
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
        map(manifest, mode, header, populates, map);
        try (final FileInputStream stream = new FileInputStream(csv)) {
            service.loadDataset(getCurrentProject(), manifest, stream).get();
        }
        return "Loaded dataset: " + dataset;
    }

    public static void map(final DatasetManifest manifest, final UploadMode mode, final String[] csvHeader,
                           final String populates, final String map) {
        if (populates != null) {
            final String[] pop = populates.split(",");
            if (pop.length != csvHeader.length) {
                throw new IllegalArgumentException("CSV header items " + csvHeader.length + " != populates items " + pop.length);
            }
            final HashMap<String, Part> parts = new HashMap<>(manifest.getParts().size());
            for (Part part: manifest.getParts()) {
                for (String p: part.getPopulates()) {
                    parts.put(p, part);
                }
            }
            if (csvHeader.length != parts.size()) {
                throw new IllegalArgumentException("CSV header items " + csvHeader.length + " != dataset populates " + parts.size());
            }
            // todo this should be part of Manifest class
            for (int i = 0; i< csvHeader.length; i++) {
                final Part part = parts.get(pop[i]);
                if (part == null) {
                    throw new IllegalArgumentException("Unknown part for populate " + pop[i]);
                }
                part.setColumnName(csvHeader[i]);
                part.setUploadMode(mode.name());
            }
        } else {
            final Map<String, String> extraMap = new HashMap<>();
            if (map != null) {
                for (String pair: delimitedListToStringArray(map, ",")) {
                    final String[] strings = delimitedListToStringArray(pair, "=");
                    if (strings.length != 2) {
                        continue;
                    }
                    extraMap.put(strings[0], strings[1]);
                }
            }
            final HashSet<String> columns = new HashSet<>(asList(csvHeader));
            for (Part part: manifest.getParts()) {
                if (part.getPopulates() == null || part.getPopulates().size() != 1) {
                    throw new IllegalStateException("Only parts with exactly one populates are supported " + part.getPopulates());
                }
                final String field = part.getPopulates().iterator().next();
                final String column;
                if (columns.contains(field)) {
                    column = field;
                } else if (extraMap.containsKey(field)) {
                    column = extraMap.get(field);
                } else {
                    final String[] items = delimitedListToStringArray(field, ".");
                    final String last = items[items.length - 1];
                    if (columns.contains(last)) {
                        column = last;
                    } else if (extraMap.containsKey(last)) {
                        column = extraMap.get(last);
                    } else {
                        throw new IllegalStateException("Unable to map field '" + field + "' ('" + last + "'). " +
                                "CSV columns: " + columns + " Extra mapping: " + extraMap);
                    }
                }
                part.setColumnName(column);
                part.setUploadMode(mode.name());
            }
        }
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
