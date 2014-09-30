package cz.geek.gooddata.shell.commands;

import com.gooddata.dataload.processes.Process;
import com.gooddata.dataload.processes.ProcessExecution;
import com.gooddata.dataload.processes.ProcessExecutionDetail;
import com.gooddata.dataload.processes.ProcessService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
@Component
public class ProcessCommand extends GoodDataCommand {

    @Autowired
    public ProcessCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"process upload", "process download", "process list", "process execute"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }

    @CliCommand(value = "process list", help = "List processes")
    public String list() {
        final Collection<Process> processes = holder.getGoodData().getProcessService().listProcesses(holder.getCurrentProject());
        final List<String> result = new ArrayList<>();
        for (Process dataset: processes) {
            result.add(dataset.getSelfLink() + " " + dataset.getName() + " " + dataset.getExecutables());
        }
        return StringUtils.collectionToDelimitedString(result, "\n");
    }


    @CliCommand(value = "process upload", help = "Create or update process")
    public String load(
            @CliOption(key = {"process"}, mandatory = false, help = "Process URI") final String processUri,
            @CliOption(key = {"name"}, mandatory = false, help = "Process name") String name,
            @CliOption(key = {"type"}, mandatory = false, help = "Process type") String type,
            @CliOption(key = {"source"}, mandatory = false, help = "Process file or directory") File data) {

        final ProcessService service = holder.getGoodData().getProcessService();
        Process process;
        if (processUri != null) {
            process = service.getProcessByUri(processUri);
            if (name != null) {
                process.setName(name);
            }
            if (type != null) {
                process.setType(type);
            }
            try {
                process = service.updateProcess(holder.getCurrentProject(), process, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            process = service.createProcess(holder.getCurrentProject(), new Process(name, type), data);
        }

        return "Uploaded process: " + process.getSelfLink();
    }


    @CliCommand(value = "process download", help = "Download process")
    public String download(@CliOption(key = {"process"}, mandatory = true, help = "Process URI") final String processUri,
                           @CliOption(key = {"target"}, mandatory = false, help = "Target dir") final String target) throws FileNotFoundException {
        final ProcessService service = holder.getGoodData().getProcessService();
        final Process process = service.getProcessByUri(processUri);
        service.getProcessSource(process, new FileOutputStream(target));
        return "Downloaded.";
    }

    @CliCommand(value = "process execute", help = "Execute process")
    public String execute(@CliOption(key = {"process"}, mandatory = true, help = "Process URI") final String processUri,
                          @CliOption(key = {"executable"}, mandatory = true, help = "Executable") final String executable,
                          @CliOption(key = {"log"}, mandatory = false, help = "Show execution log",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "false") final boolean log) {
        final ProcessService service = holder.getGoodData().getProcessService();
        final Process process = service.getProcessByUri(processUri);
        final ProcessExecutionDetail detail = service.executeProcess(new ProcessExecution(process, executable)).get();
        // todo execution uri
        String result = "Executed process: " + detail.getStatus();
        if (log) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            service.getExecutionLog(detail, out);
            result += "\n" + out.toString();
        }
        return result;
    }

}
