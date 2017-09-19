package cz.geek.gooddata.shell.output;

import java.util.List;

@FunctionalInterface
public interface RowExtractor<T> {

    List<?> extract(T row);
}
