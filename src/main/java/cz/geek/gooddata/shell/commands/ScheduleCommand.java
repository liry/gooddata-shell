package cz.geek.gooddata.shell.commands;

import com.gooddata.dataload.processes.DataloadProcess;
import com.gooddata.dataload.processes.ProcessService;
import com.gooddata.dataload.processes.Schedule;
import com.gooddata.dataload.processes.ScheduleState;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

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

    @CliAvailabilityIndicator({"schedule list", "schedule create", "schedule "})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }

    @CliCommand(value = "schedule list", help = "List schedules")
    public String list() {
        return print(getGoodData().getProcessService().listSchedules(getCurrentProject()),
                asList("URI", "State", "Cron", "Process ID", "Executables"),
                s -> asList(s.getUri(), s.getState(), s.getCron(), s.getProcessId(), s.getExecutable()));
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
            @CliOption(key = {"uri", ""}, help = "URI of existing scheduler to be updated") String scheduleUri,
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

        service.updateSchedule(getCurrentProject(), schedule);
        return "Updated schedule: " + schedule.getUri();
    }

}
