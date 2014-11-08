package cz.geek.gooddata.shell.components;

import com.gooddata.GoodData;
import com.gooddata.project.Project;
import cz.geek.gooddata.shell.output.OutputFormatter;
import org.springframework.stereotype.Component;

import static com.gooddata.Validate.notNull;

@Component
public class GoodDataHolder {

    private GoodData goodData;

    private Project currentProject;

    private String host;

    private OutputFormatter outputFormatter = OutputFormatter.pretty;

    private Throwable lastException;

    private boolean printStackTrace;

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

    public OutputFormatter getOutputFormatter() {
        return outputFormatter;
    }

    public void setOutputFormatter(OutputFormatter outputFormatter) {
        this.outputFormatter = notNull(outputFormatter, "output formatter");
    }

    public void setLastException(final Throwable lastException) {
        this.lastException = lastException;
    }

    public Throwable getLastException() {
        return lastException;
    }

    public void setPrintStackTrace(final boolean printStackTrace) {
        this.printStackTrace = printStackTrace;
    }

    public boolean isPrintStackTrace() {
        return printStackTrace;
    }
}
