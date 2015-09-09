package cz.geek.gooddata.shell.output;

import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableResultSetExtractor implements ResultSetExtractor<Table> {

    public static final TableResultSetExtractor TABLE_RESULT_SET_EXTRACTOR = new TableResultSetExtractor();

    public Table extractData(final ResultSet rs) throws SQLException {
        final List<String> header = createHeader(rs.getMetaData());
        final List<List<String>> items = createRows(rs);
        return new Table(header, items, new RowExtractor<List<String>>() {
            @Override
            public List<?> extract(final List<String> row) {
                return row;
            }
        });
    }

    private List<List<String>> createRows(final ResultSet rs) throws SQLException {
        final List<List<String>> result = new ArrayList<>();
        while (rs.next()) {
            result.add(createRow(rs));
        }
        return result;

    }

    public List<String> createRow(final ResultSet rs) throws SQLException {
        final List<String> list = new ArrayList<>();
        final ResultSetMetaData metaData = rs.getMetaData();

        for (int i = 0; i < metaData.getColumnCount(); i++) {
            list.add(rs.getString(i + 1));
        }
        return list;
    }

    private List<String> createHeader(final ResultSetMetaData metaData) throws SQLException {
        final List<String> columnNames = new ArrayList<>(metaData.getColumnCount());
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnName(i + 1));
        }
        return columnNames;
    }

}
