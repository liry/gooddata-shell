package cz.geek.gooddata.shell.commands;

import com.gooddata.md.Entry;
import com.gooddata.md.ProjectDashboard;
import com.gooddata.md.ProjectDashboard.Tab;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class DashboardCommand extends AbstractGoodDataCommand {

    @Autowired
    public DashboardCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"dashboard export", "dashboard list"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }


    @CliCommand(value = "dashboard export", help = "Export dashboard")
    public String export(@CliOption(key = {"uri", ""}, help = "Dashboard URI", mandatory = true) String uri,
                         @CliOption(key = {"tab"}, help = "Tab name", mandatory = true) String tabName,
                         @CliOption(key = {"target"}, help = "Target dir") File target
    ) throws IOException {
        final ProjectDashboard dashboard = getGoodData().getMetadataService().getObjByUri(uri, ProjectDashboard.class);
        final Tab tab = dashboard.getTabByName(tabName);
        if (tab == null) {
            throw new IllegalArgumentException("Tab " + tabName + " doesn't exist: " + dashboard.getTabs());
        }

        if (target == null) {
            target = new File(tab.getIdentifier() + ".pdf");
        }

        final OutputStream output = Files.newOutputStream(target.toPath());
        getGoodData().getExportService().exportPdf(dashboard, tab, output).get();
        return "Exported to: " + target.getAbsolutePath();
    }

    @CliCommand(value = "dashboard list", help = "List dashboards")
    public String list() {
        final Collection<Entry> dashboards = getGoodData().getMetadataService().find(getCurrentProject(), ProjectDashboard.class);
        final List<Entry> entries = new ArrayList<>(dashboards);
        entries.sort((o1, o2) -> o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier()));
        return print(entries, asList("URI", "Identifier", "Title"),
                entry -> asList(entry.getUri(), entry.getIdentifier(), entry.getTitle()));
    }

}
