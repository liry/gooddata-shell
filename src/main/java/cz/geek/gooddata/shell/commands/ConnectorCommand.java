package cz.geek.gooddata.shell.commands;

import com.gooddata.FutureResult;
import com.gooddata.connector.ConnectorService;
import com.gooddata.connector.ConnectorType;
import com.gooddata.connector.Integration;
import com.gooddata.connector.ProcessStatus;
import com.gooddata.connector.Settings;
import com.gooddata.connector.Zendesk4ProcessExecution;
import com.gooddata.connector.Zendesk4Settings;
import com.gooddata.dataload.processes.DataloadProcess;
import com.gooddata.dataload.processes.ProcessExecution;
import com.gooddata.dataload.processes.ProcessExecutionDetail;
import com.gooddata.dataload.processes.ProcessService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import static java.util.Arrays.asList;

/**
 */
@Component
public class ConnectorCommand extends AbstractGoodDataCommand {

    @Autowired
    public ConnectorCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"connector execute", "connector create"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }

    @CliCommand(value = "connector create", help = "Create integration")
    public String execute(@CliOption(key = {"connector"}, mandatory = true, help = "Connector type") final ConnectorType connector,
                          @CliOption(key = {"url"}, mandatory = true, help = "API URL") final String url) {
        final ConnectorService service = getGoodData().getConnectorService();
        final Settings settings;
        switch (connector) {
            case ZENDESK4:
                settings = new Zendesk4Settings(url);
                break;
            default: throw new IllegalArgumentException("Unsupported connector " + connector);
        }
        service.createIntegration(getCurrentProject(), settings);
        return "OK";
    }

    @CliCommand(value = "connector execute", help = "Execute process")
    public String execute(@CliOption(key = {"connector"}, mandatory = true, help = "Connector type") final ConnectorType connector,
                          /*@CliOption(key = {"incremental"}, mandatory = false, help = "Incremental") final Boolean incremental,*/
                          @CliOption(key = {"wait"}, mandatory = false, help = "Show execution log",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean wait) {
        final ConnectorService service = getGoodData().getConnectorService();
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        /*if (incremental != null) {
            execution.setIncremental(incremental);
        }*/
        final FutureResult<ProcessStatus> result = service.executeProcess(getCurrentProject(), execution);
        if (wait) {
            System.out.println(result.getPollingUri());
            return result.get().getStatus().getCode();
        } else {
            return result.getPollingUri();
        }
    }

}
