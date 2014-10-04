package cz.geek.gooddata.shell.commands;

import com.gooddata.md.MetadataService;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.report.ReportExportFormat;
import com.gooddata.report.ReportService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

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
    public String report(@CliOption(key = {"uri", ""}, mandatory = true, help = "uri") String uri) {

        final MetadataService md = getGoodData().getMetadataService();
        final ReportDefinition rd = md.getObjByUri(uri, ReportDefinition.class);

        final ReportService service = getGoodData().getReportService();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.exportReport(rd, ReportExportFormat.CSV, output);
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    @CliCommand(value = "report list", help = "List reports")
    public String list() {
        return printEntries(getGoodData().getMetadataService().find(getCurrentProject(), ReportDefinition.class));
    }

}
