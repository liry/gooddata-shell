package cz.geek.gooddata.shell.commands;

import com.gooddata.md.Queryable;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.report.ReportExportFormat;
import cz.geek.gooddata.shell.components.GoodDataHolder;
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


    @CliCommand(value = "report export", help = "Export report")
    public String report(@CliOption(key = {"uri", ""}, help = "Report URI") String reportUri,
                         @CliOption(key = {"definition"}, help = "Report definition URI") String definitionUri
                        ) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        if (reportUri != null) {
            final Report report = getGoodData().getMetadataService().getObjByUri(reportUri, Report.class);
            getGoodData().getReportService().exportReport(report, ReportExportFormat.CSV, output).get();
        } else if (definitionUri != null) {
            final ReportDefinition rd = getGoodData().getMetadataService().getObjByUri(definitionUri, ReportDefinition.class);
            getGoodData().getReportService().exportReport(rd, ReportExportFormat.CSV, output).get();
        } else {
            throw new IllegalArgumentException("Report or Report definition URI must be specified");
        }

        final ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        final List<List<String>> result = new ArrayList<>();
        final List<String> header;
        try (CsvListReader reader = new CsvListReader(new InputStreamReader(input, UTF_8), STANDARD_PREFERENCE)) {
            header = reader.read();
            List<String> row;
            while ((row = reader.read()) != null) {
                result.add(row);
            }
        }
        return print(result, header, row -> row);
    }

    @CliCommand(value = "report list", help = "List reports")
    public String list(@CliOption(key = {"definition"}, help = "List definitions",
            unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean definition) {
        final Class<? extends Queryable> cls = definition ? ReportDefinition.class : Report.class;
        return printEntries(getGoodData().getMetadataService().find(getCurrentProject(), cls));
    }

}
