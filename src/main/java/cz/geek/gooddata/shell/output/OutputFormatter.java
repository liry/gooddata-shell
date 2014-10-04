package cz.geek.gooddata.shell.output;

import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static org.apache.commons.lang.StringUtils.repeat;
import static org.springframework.util.StringUtils.collectionToDelimitedString;

public enum OutputFormatter {
    pretty {
        @Override
        public String format(Table table) {
            final StringBuilder builder = new StringBuilder();
            line(builder, table.getSizes());
            builder.append("\n");
            if (table.hasHeader()) {
                row(builder, table.getHeader(), table.getSizes());
                line(builder, table.getSizes());
                builder.append("\n");
            }
            for (List<String> row: table.getRows()) {
                row(builder, row, table.getSizes());
            }
            line(builder, table.getSizes());
            return builder.toString();
        }

        private void row(StringBuilder builder, List<String> row, List<Integer> sizes) {
            builder.append("|");
            for (int i=0; i<row.size(); i++) {
                builder.append(" ");
                final String item = row.get(i);
                builder.append(item);
                final int fill = sizes.get(i) - item.length();
                builder.append(repeat(" ", fill));
                builder.append(" |");
            }
            builder.append("\n");
        }

        private void line(StringBuilder builder, List<Integer> sizes) {
            builder.append("+");
            for (int size: sizes) {
                builder.append(repeat("-", size + 2));
                builder.append("+");
            }
        }
    },
    csv {
        @Override
        public String format(Table table) {
            final StringWriter writer = new StringWriter();
            try (final CsvListWriter csv = new CsvListWriter(writer, CsvPreference.STANDARD_PREFERENCE)) {
                if (table.hasHeader()) {
                    csv.write(table.getHeader());
                }
                for (List<String> row: table.getRows()) {
                    csv.write(row);
                }
            } catch (IOException ignored) { }
            return writer.toString();
        }
    },
    tabs {
        @Override
        public String format(Table table) {
            final StringBuilder builder = new StringBuilder();
            final String delim = "\t";
            if (table.hasHeader()) {
                builder.append(collectionToDelimitedString(table.getHeader(), delim));
            }
            for (List<String> row: table.getRows()) {
                builder.append(collectionToDelimitedString(row, delim)).append("\n");
            }
            return builder.toString();
        }
    }
    ;

    public abstract String format(Table table);
}
