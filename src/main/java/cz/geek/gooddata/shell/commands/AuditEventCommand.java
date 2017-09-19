/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package cz.geek.gooddata.shell.commands;

import com.gooddata.account.Account;
import com.gooddata.auditevent.AuditEvent;
import com.gooddata.auditevent.AuditEventPageRequest;
import com.gooddata.auditevent.AuditEventService;
import com.gooddata.collections.PageableList;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.output.RowExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Access audit events
 */
@Component
public class AuditEventCommand extends AbstractGoodDataCommand {

    @Autowired
    public AuditEventCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"audit"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }

    @CliCommand(value = "audit", help = "List audit events")
    public String list(@CliOption(key = "account", help = "User (or current user if none given)") String account,
                       @CliOption(key = "domain", help = "Domain (or current user used if none given)") String domain,
                       @CliOption(key = "type", help = "Event type") String type) {
        final AuditEventPageRequest request = new AuditEventPageRequest();
        if (type != null) {
            request.setType(type);
        }

        final AuditEventService service = getGoodData().getAuditEventService();
        final PageableList<AuditEvent> events;
        if (domain != null) {
            events = service.listAuditEvents(domain);
        } else {
            final Account a = account != null ? getAccount(account) : holder.getCurrentAccount();
            events = service.listAuditEvents(a);
        }

        return print(events, asList("Occurred", "Type", "Login", "IP"), new RowExtractor<AuditEvent>() {
            @Override
            public List<?> extract(AuditEvent event) {
                return asList(event.getOccurred(), event.getType(), event.getUserLogin(), event.getUserIp());
            }
        });
    }
}
