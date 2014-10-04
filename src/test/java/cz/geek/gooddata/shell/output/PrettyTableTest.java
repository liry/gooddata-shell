package cz.geek.gooddata.shell.output;

import cz.geek.gooddata.shell.output.RowExtractor;
import cz.geek.gooddata.shell.output.Table;
import org.junit.Test;

import java.util.List;

import static cz.geek.gooddata.shell.output.OutputFormatter.pretty;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PrettyTableTest {

    private final RowExtractor<String> singleNoHeader = new RowExtractor<String>() {
        @Override
        public List<?> extract(String row) {
            return asList(row);
        }
    };

    @Test
    public void shouldPrintOneCellTable() throws Exception {
        final Table table = new Table(asList("a"), singleNoHeader);
        assertThat(pretty.format(table), is(
                "+---+\n" +
                "| a |\n" +
                "+---+"
        ));
    }

    @Test
    public void shouldPrintTwoRowsTable() throws Exception {
        final Table table = new Table(asList("a", "aa"), singleNoHeader);
        assertThat(pretty.format(table), is(
                "+----+\n" +
                "| a  |\n" +
                "| aa |\n" +
                "+----+"
        ));
    }

    private final RowExtractor<String> doubleNoHeader = new RowExtractor<String>() {
        @Override
        public List<?> extract(String row) {
            return asList(row, row);
        }
    };

    @Test
    public void shouldPrintTwoColsTable() throws Exception {
        final Table table = new Table(asList("a"), doubleNoHeader);
        assertThat(pretty.format(table), is(
                "+---+---+\n" +
                "| a | a |\n" +
                "+---+---+"
        ));
    }

    @Test
    public void shouldPrintTwoRowsTwoColsTable() throws Exception {
        final Table table = new Table(asList("a", "aa"), doubleNoHeader);
        assertThat(pretty.format(table), is(
                "+----+----+\n" +
                "| a  | a  |\n" +
                "| aa | aa |\n" +
                "+----+----+"
        ));
    }

    private final RowExtractor<String> singleHeader = new RowExtractor<String>() {
        @Override
        public List<?> extract(String row) {
            return asList(row);
        }
    };

    @Test
    public void shouldPrintTableWithHeader() throws Exception {
        final Table table = new Table(asList("aaa"), asList("a"), singleHeader);
        assertThat(pretty.format(table), is(
                "+-----+\n" +
                "| aaa |\n" +
                "+-----+\n" +
                "| a   |\n" +
                "+-----+"
        ));
    }

}