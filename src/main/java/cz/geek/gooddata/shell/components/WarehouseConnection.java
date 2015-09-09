package cz.geek.gooddata.shell.components;

import com.gooddata.warehouse.Warehouse;
import cz.geek.gooddata.shell.components.MyGoodData.Credentials;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static com.gooddata.util.Validate.notNull;

public class WarehouseConnection {

    private final Warehouse warehouse;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public WarehouseConnection(final Warehouse warehouse, final Credentials credentials) {
        notNull(credentials, "credentials");
        this.warehouse = notNull(warehouse, "warehouse");

        final String con = warehouse.getJdbcConnectionString();
        this.dataSource = new DriverManagerDataSource(con, credentials.getUser(), credentials.getPass());
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
