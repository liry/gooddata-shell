/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package cz.geek.gooddata.shell.commands;

import com.gooddata.project.Environment;
import com.gooddata.warehouse.Warehouse;
import com.gooddata.warehouse.WarehouseService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

@Component
public class AdsCommand extends AbstractGoodDataCommand {

    @Autowired
    public AdsCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"ads create", "ads list"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }


    @CliCommand(value = "ads list", help = "List ADS instances")
    public String list() {
        return print(getGoodData().getWarehouseService().listWarehouses(), asList("JDBC URI", "Title"), new RowExtractor<Warehouse>() {
            @Override
            public List<?> extract(final Warehouse warehouse) {
                return asList(warehouse.getJdbcConnectionString(), warehouse.getTitle());
            }
        });
    }

    @CliCommand(value = "ads create", help = "Create ADS instances")
    public String create(@CliOption(key = "title", mandatory = true, help = "Title") String title,
                         @CliOption(key = "token", mandatory = true, help = "Token") String token,
                         @CliOption(key = "description", help = "Description") String description,
                         @CliOption(key = "env", help = "Environment") Environment env) {
        final Warehouse warehouse = new Warehouse(title, token);
        warehouse.setDescription(description);
        if (env != null) {
            warehouse.setEnvironment(env);
        }
        return "Created ADS instance: " + getGoodData().getWarehouseService().createWarehouse(warehouse).get().getUri();
    }

    @CliCommand(value = "ads delete", help = "Delete ADS instances")
    public String delete(@CliOption(key = "id", mandatory = true, help = "instance id") String id) {
        final WarehouseService service = getGoodData().getWarehouseService();
        final Warehouse warehouse = service.getWarehouseById(id);
        service.removeWarehouse(warehouse);
        return "Deleted ADS instance: " + id;
    }
}
