package cz.geek.gooddata.shell.commands;

import com.gooddata.md.Attribute;
import com.gooddata.md.Entry;
import com.gooddata.md.Fact;
import com.gooddata.md.Queryable;
import com.gooddata.md.Restriction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static cz.geek.gooddata.shell.Utils.entryCollectionToString;

/**
 */
@Component
public class MdCommand extends GoodDataCommand {

    static enum MdType {
        attribute(Attribute.class), fact(Fact.class);

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
    public String md(@CliOption(key = {"find"}, mandatory = true, help = "type") MdType type,
                     @CliOption(key = {"id"}, mandatory = false, help = "identifier") String id) {
        final List<Restriction> restrictions = new ArrayList<>();
        if (StringUtils.hasText(id)) {
            restrictions.add(Restriction.identifier(id));
        }
        final Restriction[] restr = restrictions.toArray(new Restriction[restrictions.size()]);
        final Collection<Entry> entries = holder.getGoodData().getMetadataService().find(holder.getCurrentProject(), type.cls, restr);
        return entryCollectionToString(entries);
    }

}
