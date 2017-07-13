/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.collections.PageRequest;
import com.gooddata.collections.PageableList;
import com.gooddata.project.Environment;
import com.gooddata.warehouse.Warehouse;
import com.gooddata.warehouse.WarehouseService;
import com.gooddata.warehouse.WarehouseUser;
import com.gooddata.warehouse.WarehouseUserRole;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.components.WarehouseConnection;
import cz.geek.gooddata.shell.output.RowExtractor;
import cz.geek.gooddata.shell.output.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.List;

import static cz.geek.gooddata.shell.output.TableResultSetExtractor.TABLE_RESULT_SET_EXTRACTOR;
import static java.util.Arrays.asList;

@Component
public class AdsCommand extends AbstractGoodDataCommand {

    @Autowired
    public AdsCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"ads create", "ads list", "ads use"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }

    @CliAvailabilityIndicator({"ads query", "ads execute", "ads users", "ads useradd", "ads userdel"})
    public boolean isJdbcAvailable() {
        return holder.hasCurrentWarehouse();
    }

    @CliCommand(value = "ads list", help = "List ADS instances")
    public String list() {
        return print(getWarehouseService().listWarehouses(), asList("JDBC URI", "Title"), new RowExtractor<Warehouse>() {
            @Override
            public List<?> extract(final Warehouse warehouse) {
                return asList(warehouse.getConnectionUrl(), warehouse.getTitle());
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
        return "Created ADS instance: " + getWarehouseService().createWarehouse(warehouse).get().getUri();
    }

    @CliCommand(value = "ads delete", help = "Delete ADS instances")
    public String delete(@CliOption(key = {""}, mandatory = true, help = "Warehouse id, uri or jdbc connection string") String name) {
        final WarehouseService service = getWarehouseService();
        final Warehouse warehouse = getWarehouse(name);
        service.removeWarehouse(warehouse);
        return "Deleted ADS instance: " + warehouse.getId();
    }

    @CliCommand(value = "ads use", help = "Use ADS instances")
    public String use(@CliOption(key = {""}, mandatory = true, help = "Warehouse id, uri or jdbc connection string") String name) {
        final Warehouse warehouse = getWarehouse(name);
        holder.setCurrentWarehouse(warehouse);
        return warehouse.getId();
    }

    @CliCommand(value = "ads query", help = "Query (select)")
    public String query(@CliOption(key = {"", "sql"}, mandatory = true, help = "SQL query returning a result set") String sql) {
        final Table table = getCurrentWarehouseConnection().getJdbcTemplate().query(sql, TABLE_RESULT_SET_EXTRACTOR);
        return print(table);
    }

    @CliCommand(value = "ads execute", help = "Execute (create, insert, update, delete,...)")
    public void execute(@CliOption(key = {"", "sql"}, mandatory = true, help = "SQL command without result") String sql) {
        getCurrentWarehouseConnection().getJdbcTemplate().execute(sql);
    }

    @CliCommand(value = "ads users", help = "List ADS users")
    public String users() {
        final Warehouse warehouse = getCurrentWarehouseConnection().getWarehouse();
        final PageableList<WarehouseUser> users = getWarehouseService().listWarehouseUsers(warehouse, new PageRequest());
        return print(users, asList("Id", "Role"), new RowExtractor<WarehouseUser>() {
            @Override
            public List<?> extract(final WarehouseUser user) {
                return asList(user.getUri(), user.getRole());
            }
        });
    }

    @CliCommand(value = "ads useradd", help = "Add user to ADS")
    public String addUser(@CliOption(key = "account", mandatory = true, help = "Account id or uri") String name,
                          @CliOption(key = "role", mandatory = true, help = "Role") WarehouseUserRole role) {
        final Account account = getAccount(name);
        final WarehouseUser user = WarehouseUser.createWithlogin(account.getLogin(), role);
        getWarehouseService().addUserToWarehouse(getCurrentWarehouseConnection().getWarehouse(), user);
        return "Added";
    }

    @CliCommand(value = "ads userdel", help = "Remove user from ADS")
    public String delUser(@CliOption(key = {"", "account"}, mandatory = true, help = "Account id or uri") String name) {
        final Account account = getAccount(name);
        final WarehouseUser user = findUser(account);
        getWarehouseService().removeUserFromWarehouse(user);
        return "Removed";
    }

    private WarehouseUser findUser(final Account account) {
        final PageableList<WarehouseUser> users = getWarehouseService().listWarehouseUsers(getCurrentWarehouseConnection().getWarehouse(), new PageRequest());
        for (WarehouseUser user: users) {
            if (user.getProfile().equals(account.getUri())) {
                return user;
            }
        }
        throw new IllegalArgumentException("User not found");
    }
    private WarehouseService getWarehouseService() {
        return getGoodData().getWarehouseService();
    }

    private WarehouseConnection getCurrentWarehouseConnection() {
        return holder.getCurrentWarehouseConnection();
    }

    private Warehouse getWarehouse(final String warehouse) {
        final WarehouseService service = getWarehouseService();
        if (Warehouse.TEMPLATE.matches(warehouse)) {
            return service.getWarehouseByUri(warehouse);
        } else if (Warehouse.JDBC_CONNECTION_TEMPLATE.matches(warehouse)) {
            final String id = Warehouse.JDBC_CONNECTION_TEMPLATE.match(warehouse).get("id");
            return service.getWarehouseById(id);
        } else {
            return service.getWarehouseById(warehouse);
        }
    }

}
