package cz.geek.gooddata.shell.commands;

import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.shell.core.CommandMarker;

import static com.gooddata.Validate.notNull;

public abstract class GoodDataCommand implements CommandMarker {

    protected final GoodDataHolder holder;

    public GoodDataCommand(final GoodDataHolder holder) {
        this.holder = notNull(holder, "holder");
    }
}
