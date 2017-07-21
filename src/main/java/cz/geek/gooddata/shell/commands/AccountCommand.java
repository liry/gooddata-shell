package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.components.Utils;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Component
public class AccountCommand extends AbstractGoodDataCommand {

    @Autowired
    public AccountCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"account create", "account get", "account delete", "account update"})
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
        final Account account = getAccountService().createAccount(new Account(email, pass, first, last), domain);
        return "Created account: " + account.getUri();
    }

    @CliCommand(value = "account update", help = "Update account")
    public String update(
            @CliOption(key = {""}, help = "Account id or uri or current account if not given") String account,
            @CliOption(key = {"first"},help = "First name") String first,
            @CliOption(key = {"last"}, help = "Last name") String last,
            @CliOption(key = {"ips"}, help = "IP whitelist (comma separated)") String ip
    ) throws IOException {
        final Account a = account != null ? getAccount(account) : getAccountService().getCurrent();
        a.setEmail(null); // todo this is a bit weird, but doesn't work otherwise
        if (first != null) {
            a.setFirstName(first);
        }
        if (last != null) {
            a.setLastName(last);
        }
        if (ip != null) {
            a.setIpWhitelist(Arrays.asList(StringUtils.split(ip, ",")));
        }
        getAccountService().updateAccount(a);
        return "Updated account: " + a.getUri();
    }

    @CliCommand(value = "account get", help = "Get account")
    public String project(@CliOption(key = {""}, help = "Account id or uri or current account if not given") String account) {
        final Account a = account != null ? getAccount(account) : getAccountService().getCurrent();
        return print(singletonList(a));
    }

    @CliCommand(value = "account delete", help = "Delete account")
    public String delete(@CliOption(key = {""}, mandatory = true, help = "Account id or uri") String account) {
        final Account a = getAccount(account);
        getAccountService().removeAccount(a);
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

    private AccountService getAccountService() {
        return getGoodData().getAccountService();
    }

}
