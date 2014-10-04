package cz.geek.gooddata.shell.output;

import java.util.List;

public interface RowExtractor<T> {

    List<?> extract(T row);
}
