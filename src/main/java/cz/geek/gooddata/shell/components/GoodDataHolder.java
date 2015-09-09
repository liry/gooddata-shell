package cz.geek.gooddata.shell.components;

import com.gooddata.project.Project;
import com.gooddata.warehouse.Warehouse;
import cz.geek.gooddata.shell.components.MyGoodData.Credentials;
import cz.geek.gooddata.shell.output.OutputFormatter;
import org.springframework.stereotype.Component;

import static com.gooddata.util.Validate.notNull;

@Component
public class GoodDataHolder {

    private Credentials credentials;

    private MyGoodData goodData;

    private WarehouseConnection connection;

    private Project currentProject;

    private String shortHost;

    private OutputFormatter outputFormatter = OutputFormatter.pretty;

    private Throwable lastException;

    private boolean printStackTrace;

    public MyGoodData getGoodData() {
        notNull(goodData, "gooddata");
        return goodData;
    }

    public void login(final Credentials credentials) {
        this.credentials = credentials;
        goodData = new MyGoodData(credentials);
        this.shortHost = credentials.getHost() == null ? "secure" : credentials.getHost().substring(0, credentials.getHost().indexOf('.'));
        this.currentProject = null;
        this.connection = null;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public boolean hasCredentials() {
        return credentials != null;
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

    public void setCurrentWarehouse(final Warehouse warehouse) {
        notNull(warehouse, "warehouse");
        this.connection = new WarehouseConnection(warehouse, getCredentials());
    }

    public WarehouseConnection getCurrentWarehouse() {
        return connection;
    }

    public boolean hasCurrentWarehouse() {
        return connection != null;
    }

    public boolean hasGoodData() {
        return goodData != null;
    }

    public String getShortHost() {
        return shortHost;
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
