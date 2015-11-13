/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package cz.geek.gooddata.shell.commands;

import static java.util.Arrays.asList;

import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;

import com.gooddata.connector.ConnectorService;
import com.gooddata.connector.ConnectorType;
import com.gooddata.connector.Settings;
import com.gooddata.gdc.FeatureFlag;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectFeatureFlag;
import com.gooddata.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FeatureFlagCommand to setup feature flags
 */
@Component
public class FeatureCommand extends AbstractGoodDataCommand {

    @Autowired
    public FeatureCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"feature --project set", "feature --project list", "feature list"})
    public boolean isAvailableForProject() {
        return holder.hasCurrentProject();
    }

    @CliCommand(value = "feature --project list", help = "List feature flags for project")
    public String listProject() {
        final ProjectService projectService = getGoodData().getProjectService();
        return print(projectService.listFeatureFlags(getCurrentProject()), asList("flag", "value"), new RowExtractor<ProjectFeatureFlag>() {
            @Override
            public List<?> extract(ProjectFeatureFlag flag) {
                return asList(flag.getName(), flag.getEnabled());
            }
        });
    }

    @CliCommand(value = "feature list", help = "List all feature flags for project")
    public String listAll() {
        final ProjectService projectService = getGoodData().getProjectService();
        return print(projectService.listAggregatedFeatureFlags(getCurrentProject()), asList("flag", "value"), new RowExtractor<FeatureFlag>() {
            @Override
            public List<?> extract(FeatureFlag flag) {
                return asList(flag.getName(), flag.getEnabled());
            }
        });
    }

    @CliCommand(value = "feature --project set", help = "set feature flag for project")
    public String setProject(@CliOption(key = {"flag"}, mandatory = true, help = "feature flag name") final String name,
            @CliOption(key = {"value"}, mandatory = true, help = "feature flag value") final boolean value) {
        final ProjectService projectService = getGoodData().getProjectService();
        ProjectFeatureFlag flag = projectService.getFeatureFlag(getCurrentProject(), name);
        if (flag != null) {
            flag.setEnabled(value);
            projectService.updateFeatureFlag(flag);
        } else {
            flag = projectService.createFeatureFlag(getCurrentProject(), new ProjectFeatureFlag(name, value));
        }
        return flag.getUri();

    }
}
