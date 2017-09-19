package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.md.Entry;
import com.gooddata.project.Project;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.components.MyGoodData;
import cz.geek.gooddata.shell.output.RowExtractor;
import cz.geek.gooddata.shell.output.Table;
import org.springframework.shell.core.ExecutionProcessor;
import org.springframework.shell.event.ParseResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;

public abstract class AbstractGoodDataCommand implements ExecutionProcessor {

    protected final GoodDataHolder holder;

    public AbstractGoodDataCommand(final GoodDataHolder holder) {
        this.holder = notNull(holder, "holder");
    }

    public MyGoodData getGoodData() {
        return holder.getGoodData();
    }

    public Project getCurrentProject() {
        return holder.getCurrentProject();
    }

    public <T> String print(Collection<T> collection, RowExtractor<T> extractor) {
        return print(collection, null, extractor);
    }

    public <T> String print(Iterable<T> collection, List<?> header, RowExtractor<T> extractor) {
        final Table table = new Table(header, collection, extractor);
        return print(table);
    }

    public String print(final Table table) {
        return holder.getOutputFormatter().format(table);
    }

    protected String printEntries(Collection<Entry> list) {
        final ArrayList<Entry> entries = new ArrayList<>(list);
        Collections.sort(entries, (o1, o2) -> o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier()));
        return print(entries, asList("URI", "Identifier", "Title"),
                entry -> asList(entry.getUri(), entry.getIdentifier(), entry.getTitle()));
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
        holder.setLastException(thrown);
        if (holder.isPrintStackTrace()) {
            thrown.printStackTrace();
        }
    }

    protected Account getAccount(final String account) {
        final AccountService service = getGoodData().getAccountService();
        return Account.TEMPLATE.matches(account) ? service.getAccountByUri(account) : service.getAccountById(account);
    }
}
