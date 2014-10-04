package cz.geek.gooddata.shell.commands;

import com.gooddata.md.report.ReportDefinition;
import com.gooddata.report.ReportExportFormat;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

/**
 */
@Component
public class ReportCommand extends AbstractGoodDataCommand {

    @Autowired
    public ReportCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"report execute", "report list"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }


    @CliCommand(value = "report execute", help = "Execute report")
    public String report(@CliOption(key = {"uri", ""}, mandatory = true, help = "uri") String uri) throws IOException {

        final ReportDefinition rd = getGoodData().getMetadataService().getObjByUri(uri, ReportDefinition.class);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        getGoodData().getReportService().exportReport(rd, ReportExportFormat.CSV, output);

        final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        final List<List<String>> result = new ArrayList<>();
        List<String> header = null;
        try (CsvListReader reader = new CsvListReader(new InputStreamReader(input, UTF_8), STANDARD_PREFERENCE)) {
            header = reader.read();
            List<String> row;
            while ((row = reader.read()) != null) {
                result.add(row);
            }
        }
        return print(result, header, new RowExtractor<List<String>>() {
            @Override
            public List<?> extract(List<String> row) {
                return row;
            }
        });
    }

    @CliCommand(value = "report list", help = "List reports")
    public String list() {
        return printEntries(getGoodData().getMetadataService().find(getCurrentProject(), ReportDefinition.class));
    }

}
