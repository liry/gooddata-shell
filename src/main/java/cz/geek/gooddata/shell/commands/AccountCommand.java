package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
public class AccountCommand extends AbstractGoodDataCommand {

    @Autowired
    public AccountCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"account create", "account get", "account delete"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }

    @CliCommand(value = "account create", help = "Create account")
    public String create(
            @CliOption(key = {"email"}, mandatory = true, help = "Email/Login") String email,
            @CliOption(key = {"pass"}, mandatory = true, help = "Password") String pass,
            @CliOption(key = {"first"}, mandatory = true, help = "First name") String first,
            @CliOption(key = {"last"}, mandatory = true, help = "Last name") String last,
            @CliOption(key = {"domain"}, mandatory = true, help = "Domain/Organization name") String domain
    ) {
        final Account account = getGoodData().getAccountService().createAccount(new Account(email, pass, first, last), domain);
        return "Created account: " + account.getUri();
    }

    @CliCommand(value = "account get", help = "Get account")
    public String project(@CliOption(key = {""}, help = "Account id or uri") String account) {
        final Account a = account != null ? getAccount(account) : getGoodData().getAccountService().getCurrent();
        return print(singletonList(a));
    }

    @CliCommand(value = "account delete", help = "Delete account")
    public String delete(@CliOption(key = {""}, mandatory = true, help = "Account id or uri") String account) {
        final Account a = getAccount(account);
        getGoodData().getAccountService().removeAccount(a);
        return "Removed " + a.getUri();
    }

    private String print(final List<Account> accounts) {
        return print(accounts, asList("uri", "login", "first name", "last name"), new RowExtractor<Account>() {
            @Override
            public List<?> extract(final Account account) {
                return asList(account.getUri(), account.getLogin(), account.getFirstName(), account.getLastName());
            }
        });
    }

}
