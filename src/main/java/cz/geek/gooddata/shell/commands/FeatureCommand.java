/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package cz.geek.gooddata.shell.commands;

import com.gooddata.GoodDataRestException;
import com.gooddata.featureflag.FeatureFlagService;
import com.gooddata.featureflag.ProjectFeatureFlag;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

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
        final FeatureFlagService service = getGoodData().getFeatureFlagService();
        return print(service.listFeatureFlags(getCurrentProject()), asList("flag", "value"),
                flag -> asList(flag.getName(), flag.isEnabled()));
    }

    @CliCommand(value = "feature list", help = "List all feature flags for project")
    public String listAll() {
        final FeatureFlagService service = getGoodData().getFeatureFlagService();
        return print(service.listFeatureFlags(getCurrentProject()), asList("flag", "value"),
                flag -> asList(flag.getName(), flag.isEnabled()));
    }

    @CliCommand(value = "feature --project set", help = "set feature flag for project")
    public String setProject(@CliOption(key = {"flag"}, mandatory = true, help = "feature flag name") final String name,
            @CliOption(key = {"value"}, mandatory = true, help = "feature flag value") final boolean value) {
        final FeatureFlagService service = getGoodData().getFeatureFlagService();
        ProjectFeatureFlag flag;
        try {
            flag = service.getProjectFeatureFlag(getCurrentProject(), name);
            flag.setEnabled(value);
            service.updateProjectFeatureFlag(flag);
            return flag.getUri();
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                flag = service.createProjectFeatureFlag(getCurrentProject(), new ProjectFeatureFlag(name, value));
            } else {
                throw e;
            }
        }

        return flag.getUri();

    }
}
