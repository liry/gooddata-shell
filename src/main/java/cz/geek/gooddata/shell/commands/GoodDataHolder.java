package cz.geek.gooddata.shell.commands;

import com.gooddata.GoodData;
import com.gooddata.project.Project;
import org.springframework.stereotype.Component;

import static com.gooddata.Validate.notNull;

@Component
public class GoodDataHolder {

    private GoodData goodData;

    private Project currentProject;

    private String host;

    public GoodData getGoodData() {
        notNull(goodData, "gooddata");
        return goodData;
    }

    public void login(final String host, final String user, final String pass) {
        goodData = host == null ? new GoodData(user, pass) : new GoodData(host, user, pass);
        this.host = host == null ? "secure" : host.substring(0, host.indexOf('.'));
    }

    public void setCurrentProject(final Project currentProject) {
        this.currentProject = currentProject;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public boolean hasCurrentProject() {
        return currentProject != null;
    }

    public boolean hasGoodData() {
        return goodData != null;
    }

    public String getHost() {
        return host;
    }
}
