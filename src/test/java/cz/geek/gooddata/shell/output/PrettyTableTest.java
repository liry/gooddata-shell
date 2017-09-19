package cz.geek.gooddata.shell.output;

import org.junit.Test;

import java.util.Collections;

import static cz.geek.gooddata.shell.output.OutputFormatter.pretty;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PrettyTableTest {

    private final RowExtractor<String> singleNoHeader = Collections::singletonList;

    @Test
    public void shouldPrintOneCellTable() throws Exception {
        final Table table = new Table(singletonList("a"), singleNoHeader);
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

    private final RowExtractor<String> doubleNoHeader = row -> asList(row, row);

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

    private final RowExtractor<String> singleHeader = Collections::singletonList;

    @Test
    public void shouldPrintTableWithHeader() throws Exception {
        final Table table = new Table(singletonList("aaa"), singletonList("a"), singleHeader);
        assertThat(pretty.format(table), is(
                "+-----+\n" +
                "| aaa |\n" +
                "+-----+\n" +
                "| a   |\n" +
                "+-----+"
        ));
    }

}