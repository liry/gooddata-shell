package cz.geek.gooddata.shell.commands;

import com.gooddata.project.CreatedInvitations;
import com.gooddata.project.Environment;
import com.gooddata.project.Invitation;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectDriver;
import com.gooddata.project.ProjectService;
import com.gooddata.project.ProjectValidationResults;
import com.gooddata.project.ProjectValidationType;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class ProjectCommand extends AbstractGoodDataCommand {

    @Autowired
    public ProjectCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"project list", "project create", "project delete", "project use", "project validate"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }

    @CliAvailabilityIndicator({"project invite"})
    public boolean isAvailableWithProject() {
        return holder.hasCurrentProject();
    }

    @CliCommand(value = "project list", help = "List GoodData projects")
    public String list() {
        return print(getProjectService().getProjects(), asList("URI", "Title"),
                project -> asList(project.getUri(), project.getTitle()));
    }

    @CliCommand(value = "project create", help = "Create GoodData project")
    public String create(
            @CliOption(key = {"title"}, mandatory = true, help = "Title") String title,
            @CliOption(key = {"token"}, mandatory = true, help = "Token") String token,
            @CliOption(key = {"template"}, help = "Title") String template,
            @CliOption(key = "env", help = "Environment") Environment env,
            @CliOption(key = "driver", help = "Driver") ProjectDriver driver
    ) {
        final Project p = new Project(title, token);
        p.setProjectTemplate(template);
        if (env != null) {
            p.setEnvironment(env);
        }
        if (driver != null) {
            p.setDriver(driver);
        }
        final Project project = getProjectService().createProject(p).get();
        holder.setCurrentProject(project);
        return "Created project: " + project.getUri();
    }

    @CliCommand(value = "project use", help = "Get or set current GoodData project")
    public String project(@CliOption(key = {""}, help = "Project id or uri") String project) {
        if (project !=  null) {
            final Project p = getProject(project);
            holder.setCurrentProject(p);
        }
        if (holder.hasCurrentProject()) {
            return "Current project: " + getCurrentProject().getUri();
        } else {
            return "No current project";
        }
    }

    @CliCommand(value = "project delete", help = "Delete GoodData project")
    public String delete(@CliOption(key = {""}, help = "Project id or uri") String project) {
        final Project p = getProject(project);
        getProjectService().removeProject(p);
        return "Removed " + p.getUri();
    }

    @CliCommand(value = "project validate", help = "Validate GoodData project")
    public String validate(@CliOption(key = {""}, help = "Project id or uri") String project) {
        final Project p = holder.hasCurrentProject() ? holder.getCurrentProject() : getProject(project);
        final ProjectValidationResults results = getProjectService().validateProject(p, ProjectValidationType.PDM_VS_DWH, ProjectValidationType.INVALID_OBJECTS, ProjectValidationType.LDM, ProjectValidationType.METRIC_FILTER).get();
        return "Valid: " + results.isValid();
    }

    @CliCommand(value = "project invite", help = "Invite user to GoodData project")
    public String invite(@CliOption(key = {""}, mandatory = true, help = "Email address") String email) {
        final CreatedInvitations created = getProjectService().sendInvitations(holder.getCurrentProject(), new Invitation(email));
        final List<String> result = new ArrayList<>();
        result.addAll(invitationResult("Invitations: ", created.getInvitationUris()));
        result.addAll(invitationResult("Already in project: ", created.getAlreadyInProjectEmails()));
        result.addAll(invitationResult("Domain mismatch: ", created.getDomainMismatchEmails()));
        return StringUtils.join(result, ", ");
    }

    private ProjectService getProjectService() {
        return getGoodData().getProjectService();
    }

    private Project getProject(final String project) {
        final ProjectService service = getProjectService();
        return Project.TEMPLATE.matches(project) ? service.getProjectByUri(project) : service.getProjectById(project);
    }

    private static List<String> invitationResult(final String item, final List<String> list) {
        final List<String> result = new ArrayList<>();
        if (!list.isEmpty()) {
            result.add(item + StringUtils.join(list, " "));
        }
        return result;
    }

}
