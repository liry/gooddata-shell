package cz.geek.gooddata.shell.commands;

import com.gooddata.FutureResult;
import com.gooddata.dataload.processes.DataloadProcess;
import com.gooddata.dataload.processes.ProcessExecutionDetail;
import com.gooddata.dataload.processes.ProcessService;
import com.gooddata.dataload.processes.Schedule;
import com.gooddata.dataload.processes.ScheduleExecution;
import com.gooddata.dataload.processes.ScheduleState;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static cz.geek.gooddata.shell.commands.ProcessCommand.pickSoleExecutable;
import static java.util.Arrays.asList;

/**
 */
@Component
public class ScheduleCommand extends AbstractGoodDataCommand {

    @Autowired
    public ScheduleCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"schedule list", "schedule create", "schedule executions", "schedule execute"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }

    @CliCommand(value = "schedule list", help = "List schedules")
    public String list() {
        // todo paging
        return print(getGoodData().getProcessService().listSchedules(getCurrentProject()), asList("URI", "State", "Cron", "Process ID", "Executable"),
                new RowExtractor<Schedule>() {
            @Override
            public List<?> extract(Schedule schedule) {
                return asList(schedule.getUri(), schedule.getState(), schedule.getCron(), schedule.getProcessId(), schedule.getExecutable());
            }
        });
    }

    @CliCommand(value = "schedule create", help = "Create schedule")
    public String create(
            @CliOption(key = {"processUri"}, mandatory = true, help = "Process uri") String processUri,
            @CliOption(key = {"cron"}, mandatory = true, help = "Cron expression") String cron,
            @CliOption(key = {"executable"}, help = "Executable") String executable) {

        final ProcessService service = getGoodData().getProcessService();
        final DataloadProcess process = service.getProcessByUri(processUri);
        executable = pickSoleExecutable(process, executable);
        final Schedule schedule = service.createSchedule(getCurrentProject(), new Schedule(process, executable, cron));

        return "Created schedule: " + schedule.getUri();
    }

    @CliCommand(value = "schedule update", help = "Update schedule")
    public String update(
            @CliOption(key = {"uri", ""}, help = "URI of existing schedule to be updated") String scheduleUri,
            @CliOption(key = {"processUri"}, help = "Process uri") String processUri,
            @CliOption(key = {"cron"}, help = "Cron expression") String cron,
            @CliOption(key = {"state"}, help = "State") ScheduleState state,
            @CliOption(key = {"executable"}, help = "Executable") String executable) {

        final ProcessService service = getGoodData().getProcessService();
        final Schedule schedule = service.getScheduleByUri(scheduleUri);
        DataloadProcess process = null;
        if (processUri != null) {
            process = service.getProcessByUri(processUri);
            schedule.setProcessId(process);
        }
        if (executable != null) {
            if (process == null) {
                process = service.getProcessById(getCurrentProject(), schedule.getProcessId());
            }
            schedule.setExecutable(process, executable);
        }
        if (cron != null) {
            schedule.setCron(cron);
        }
        if (state != null) {
            schedule.setState(state);
        }

        service.updateSchedule(schedule);
        return "Updated schedule: " + schedule.getUri();
    }

    @CliCommand(value = "schedule executions", help = "List executions of schedule")
    public String executions(@CliOption(key = {"uri", ""}, help = "URI schedule") String scheduleUri) {
        final ProcessService service = getGoodData().getProcessService();
        final Schedule schedule = service.getScheduleByUri(scheduleUri);

        final List<ScheduleExecution> list = service.listExecutions(schedule);
        return print(list, asList("URI", "Status", "Trigger"),
                new RowExtractor<ScheduleExecution>() {
                    @Override
                    public List<?> extract(ScheduleExecution execution) {
                        return asList(execution.getUri(), execution.getStatus(), execution.getTrigger());
                    }
                });
    }

    @CliCommand(value = "schedule execute", help = "Manually execute schedule")
    public String execute(@CliOption(key = {"uri", ""}, help = "URI of schedule") String scheduleUri,
                          @CliOption(key = {"wait"}, help = "Wait for completion",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean wait,
                          @CliOption(key = {"log"}, help = "Show execution log",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean log
                          ) {

        final ProcessService service = getGoodData().getProcessService();
        final Schedule schedule = service.getScheduleByUri(scheduleUri);
        final FutureResult<ScheduleExecution> futureResult = service.executeSchedule(schedule);

        if (wait || log) {
            System.out.println(futureResult.getPollingUri());
            return getScheduleExecution(futureResult);
        } else {
            return futureResult.getPollingUri();
        }
    }

    @CliCommand(value = "schedule execution", help = "Wait for schedule execution")
    public String execution(@CliOption(key = {"uri", ""}, help = "URI of schedule execution") String executionUri,
                          @CliOption(key = {"log"}, help = "Show execution log",
                                  unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") final boolean log
                          ) {

        final ProcessService service = getGoodData().getProcessService();
        final ScheduleExecution scheduleExecution = service.getScheduleExecutionByUri(executionUri);
        final FutureResult<ScheduleExecution> futureResult = service.getScheduleExecution(scheduleExecution);

        return getScheduleExecution(futureResult);
    }

    private String getScheduleExecution(final FutureResult<ScheduleExecution> futureResult) {
        final ScheduleExecution detail = futureResult.get();
        String result = "Executed schedule " + detail.getUri() + ": " + detail.getStatus();
            /*if (log) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                service.getExecutionLog(detail, out);
                result += "\n" + out.toString();
            }*/
        return result;
    }

}
