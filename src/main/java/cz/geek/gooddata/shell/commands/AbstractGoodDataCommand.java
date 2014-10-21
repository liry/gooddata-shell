package cz.geek.gooddata.shell.commands;

import com.gooddata.GoodData;
import com.gooddata.md.Entry;
import com.gooddata.project.Project;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import cz.geek.gooddata.shell.output.Table;
import org.springframework.shell.core.ExecutionProcessor;
import org.springframework.shell.event.ParseResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.gooddata.Validate.notNull;
import static java.util.Arrays.asList;

public abstract class AbstractGoodDataCommand implements ExecutionProcessor {

    protected final GoodDataHolder holder;

    public AbstractGoodDataCommand(final GoodDataHolder holder) {
        this.holder = notNull(holder, "holder");
    }

    public GoodData getGoodData() {
        return holder.getGoodData();
    }

    public Project getCurrentProject() {
        return holder.getCurrentProject();
    }

    public <T> String print(Collection<T> collection, RowExtractor<T> extractor) {
        return print(collection, null, extractor);
    }

    public <T> String print(Collection<T> collection, List<?> header, RowExtractor<T> extractor) {
        final Table table = new Table(header, collection, extractor);
        return holder.getOutputFormatter().format(table);
    }

    protected String printEntries(Collection<Entry> list) {
        final ArrayList<Entry> entries = new ArrayList<>(list);
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(final Entry o1, final Entry o2) {
                return o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier());
            }
        });
        return print(entries, asList("URI", "Identifier", "Title"), new RowExtractor<Entry>() {
            @Override
            public List<?> extract(Entry entry) {
                return asList(entry.getLink(), entry.getIdentifier(), entry.getTitle());
            }
        });
    }

    @Override
    public ParseResult beforeInvocation(final ParseResult invocationContext) {
        return invocationContext;
    }

    @Override
    public void afterReturningInvocation(final ParseResult invocationContext, final Object result) {

    }

    @Override
    public void afterThrowingInvocation(final ParseResult invocationContext, final Throwable thrown) {
        thrown.printStackTrace();
    }
}
