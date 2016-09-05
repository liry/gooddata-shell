package cz.geek.gooddata.shell.commands;

import com.gooddata.md.Attribute;
import com.gooddata.md.Entry;
import com.gooddata.md.Fact;
import com.gooddata.md.Metric;
import com.gooddata.md.Queryable;
import com.gooddata.md.Restriction;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
@Component
public class MdCommand extends AbstractGoodDataCommand {

    static enum MdType {
        attribute(Attribute.class), fact(Fact.class), metric(Metric.class),
        report(Report.class), reportdefinition(ReportDefinition.class);

        private final Class<? extends Queryable> cls;

        MdType(final Class<? extends Queryable> cls) {
            this.cls = cls;
        }
    }

    @Autowired
    public MdCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"md"})
    public boolean isAvailable() {
        return holder.hasCurrentProject();
    }


    @CliCommand(value = "md", help = "Query metadata")
    public String md(@CliOption(key = {"type"}, help = "type") MdType type,
                     @CliOption(key = {"id"}, help = "identifier") String id,
                     @CliOption(key = {"title"}, help = "title") String title,
                     @CliOption(key = {"summary"}, help = "summary") String summary
                     ) {
        final List<Restriction> restrictions = new ArrayList<>();
        if (StringUtils.hasText(id)) {
            restrictions.add(Restriction.identifier(id));
        }
        if (StringUtils.hasText(title)) {
            restrictions.add(Restriction.title(title));
        }
        if (StringUtils.hasText(summary)) {
            restrictions.add(Restriction.summary(summary));
        }
        final Restriction[] restr = restrictions.toArray(new Restriction[restrictions.size()]);
        final List<Entry> entries = new ArrayList<>();
        for (MdType t: type != null ? new MdType[]{type} : MdType.values()) {
            final Collection<Entry> result = getGoodData().getMetadataService().find(getCurrentProject(), t.cls, restr);
            entries.addAll(result);
        }
        return printEntries(entries);
    }

}
