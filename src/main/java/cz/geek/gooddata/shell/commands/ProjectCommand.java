package cz.geek.gooddata.shell.commands;

import com.gooddata.project.Project;
import com.gooddata.project.ProjectService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ProjectCommand extends AbstractGoodDataCommand {

    @Autowired
    public ProjectCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"project list", "project create", "project use"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }

    @CliCommand(value = "project list", help = "List GoodData projects")
    public String list() {
        final StringBuilder builder = new StringBuilder();
        for (Project project: getGoodData().getProjectService().getProjects()) {
            builder.append(project.getSelfLink()).append(' ').append(project.getTitle()).append('\n');
        }
        return builder.toString();
    }

    @CliCommand(value = "project create", help = "Create GoodData project")
    public String create(
            @CliOption(key = {"title"}, mandatory = true, help = "Title") String title,
            @CliOption(key = {"token"}, mandatory = true, help = "Token") String token,
            @CliOption(key = {"template"}, mandatory = false, help = "Title") String template
    ) {
        final Project p = new Project(title, token);
        p.setProjectTemplate(template);
        final Project project = getGoodData().getProjectService().createProject(p).get();
        holder.setCurrentProject(project);
        return "Created project: " + project.getSelfLink();
    }

    @CliCommand(value = "project use", help = "Get or set current GoodData project")
    public String project(@CliOption(key = {""}, mandatory = false, help = "Project id or uri") String projectId) {
        if (projectId !=  null) {

            final ProjectService service = getGoodData().getProjectService();
            final Project project = Project.TEMPLATE.matches(projectId) ? service.getProjectByUri(projectId) : service.getProjectById(projectId);
            holder.setCurrentProject(project);
        }
        if (holder.hasCurrentProject()) {
            return "Current project: " + getCurrentProject().getSelfLink();
        } else {
            return "No current project";
        }
    }

}
