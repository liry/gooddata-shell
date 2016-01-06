package cz.geek.gooddata.shell.commands;

import com.gooddata.FutureResult;
import com.gooddata.dataload.processes.DataloadProcess;
import com.gooddata.dataload.processes.ProcessExecution;
import com.gooddata.dataload.processes.ProcessExecutionDetail;
import com.gooddata.dataload.processes.ProcessService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 */
@Component
public class ProcessCommand extends AbstractGoodDataCommand {

    @Autowired
    public ProcessCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"process upload", "process download", "process list", "process execute"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }

    @CliAvailabilityIndicator("process listuser")
    public boolean isAvailableListUser() {
        return holder.hasGoodData();
    }

    @CliCommand(value = "process list", help = "List processes")
    public String list() {
        final Collection<DataloadProcess> processes = getGoodData().getProcessService().listProcesses(getCurrentProject());
        return list(processes);
    }

    @CliCommand(value = "process listuser", help = "List all user's processes")
    public String listUser() {
        final Collection<DataloadProcess> processes = getGoodData().getProcessService().listUserProcesses();
        return list(processes);
    }

    private String list(final Collection<DataloadProcess> processes) {
        return print(processes, asList("URI", "Name", "Executables"),
                new RowExtractor<DataloadProcess>() {
            @Override
            public List<?> extract(DataloadProcess process) {
                return asList(process.getUri(), process.getName(), process.getExecutables());
            }
        });
    }

    @CliCommand(value = "process upload", help = "Create or update process")
    public String load(
            @CliOption(key = {"uri", ""}, help = "Process URI of existing process to be updated") String processUri,
            @CliOption(key = {"name"}, help = "Process name") String name,
            @CliOption(key = {"type"}, help = "Process type") String type,
            @CliOption(key = {"source"}, mandatory = true, help = "Process file or directory") File source) {

        final ProcessService service = getGoodData().getProcessService();
        DataloadProcess process;
        if (processUri != null) {
            process = service.getProcessByUri(processUri);
            if (name != null) {
                process.setName(name);
            }
            if (type != null) {
                process.setType(type);
            }
            process = service.updateProcess(process, source);
        } else {
            if (name == null) {
                name = removeExtension(source.getName());
            }
            process = service.createProcess(getCurrentProject(), new DataloadProcess(name, type), source);
        }

        return "Uploaded process: " + process.getUri();
    }


    @CliCommand(value = "process download", help = "Download process")
    public String download(@CliOption(key = {"", "uri"}, mandatory = true, help = "Process URI") String processUri,
                           @CliOption(key = {"target"}, help = "Target dir") File target) throws FileNotFoundException {
        final ProcessService service = getGoodData().getProcessService();
        final DataloadProcess process = service.getProcessByUri(processUri);
        if (target == null) {
            target = new File(process.getName() + ".zip");
        }
        service.getProcessSource(process, new FileOutputStream(target));
        return "Downloaded to " + target.getAbsolutePath();
    }

    @CliCommand(value = "process execute", help = "Execute process")
    public String execute(@CliOption(key = {"", "uri"}, mandatory = true, help = "Process URI") final String processUri,
                          @CliOption(key = {"executable"}, help = "Executable") String executable,
                          @CliOption(key = {"param"}, help = "Parameters (name and value seprated by '=')")
                                  Pair<String, String>[] params,
                          @CliOption(key = {"hidden"}, help = "Hidden parameters (name and value seprated by '=')")
                                  Pair<String, String>[] hidden,
                          @CliOption(key = {"wait"}, help = "Wait for completion",
                                   unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean wait,
                          @CliOption(key = {"log"}, help = "Show execution log",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean log,
                          @CliOption(key = "scriptNextVersion", help = "Use 'next version' environment for scripts",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean scriptNextVersion,
                          @CliOption(key = "cloverNextVersion", help = "Use 'next version' environment for graphs",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean cloverNextVersion,
                          @CliOption(key = "newGraphExecution", help = "Run graph on scripts environment",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean newGraphExecution
                          ) {
        final ProcessService service = getGoodData().getProcessService();
        final DataloadProcess process = service.getProcessByUri(processUri);

        executable = pickSoleExecutable(process, executable);

        final Map<String, String> paramMap = createMap(params);
        if (scriptNextVersion) {
            paramMap.put("scriptNextVersion", "true");
        }
        if (cloverNextVersion) {
            paramMap.put("cloverNextVersion", "true");
        }
        if (newGraphExecution) {
            paramMap.put("newGraphExecution", "true");
        }

        final Map<String, String> hiddenMap = createMap(hidden);

        final ProcessExecution execution = new ProcessExecution(process, executable, paramMap, hiddenMap);
        final FutureResult<ProcessExecutionDetail> futureResult = service.executeProcess(execution);
        if (wait || log) {
            System.out.println(futureResult.getPollingUri());
            final ProcessExecutionDetail detail = futureResult.get();
            String result = "Executed process " + detail.getUri() + ": " + detail.getStatus();
            if (log) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                service.getExecutionLog(detail, out);
                result += "\n" + out.toString();
            }
            return result;
        } else {
            return futureResult.getPollingUri();
        }
    }

    private Map<String, String> createMap(Pair<String, String>[] params) {
        final Map<String, String> paramMap = new LinkedHashMap<>();
        if (params != null) {
            for (Pair<String, String> param: params) {
                paramMap.put(param.getKey(), param.getValue());
            }
        }
        return paramMap;
    }

    static String pickSoleExecutable(final DataloadProcess process, final String executable) {
        if (isEmpty(executable)) {
            final Set<String> executables = process.getExecutables();
            if (executables.size() == 1) {
                return executables.iterator().next();
            } else {
                throw new IllegalArgumentException("Need to specify 'executable' option with value of " + executables);
            }
        }
        return executable;
    }

}
