package cz.geek.gooddata.shell.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.Assert.notNull;

public class Table {

    private final List<String> header;
    private final List<List<String>> rows;
    private final List<Integer> sizes = new ArrayList<>();

    public <T> Table(Collection<T> items, RowExtractor<T> formatter) {
        this(null, items, formatter);
    }

    public <T> Table(List<?> header, Collection<T> items, RowExtractor<T> formatter) {
        notNull(formatter);
        notNull(items);
        rows = new ArrayList<>(items.size());

        this.header = header == null || header.isEmpty() ? Collections.<String>emptyList() : createRow(header);

        for (T item: items) {
            addRow(formatter.extract(item));
        }
    }

    public final void addRow(List<?> row) {
        notNull(row);
        final List<String> strings = createRow(row);
        rows.add(strings);
    }

    private List<String> createRow(List<?> row) {
        final List<String> strings = new ArrayList<>(row.size());
        for (int i=0; i<row.size(); i++) {
            final Object item = row.get(i);
            final String string = item != null ? item.toString() : "";
            strings.add(string);
            size(i, item);
        }
        return strings;
    }

    private void size(int index, Object item) {
        final int size = item != null ? item.toString().length() : 0;
        if (sizes.size() <= index) {
            sizes.add(size);
        } else {
            final int current = sizes.get(index);
            if (size > current) {
                sizes.set(index, size);
            }
        }
    }

    public List<String> getHeader() {
        return header;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    public boolean hasHeader() {
        return !header.isEmpty();
    }

    @Override
    public String toString() {
        return OutputFormatter.pretty.format(this);
    }
}
