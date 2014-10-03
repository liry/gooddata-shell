package cz.geek.gooddata.shell.commands;

import com.gooddata.GoodData;
import com.gooddata.project.Project;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.shell.core.CommandMarker;

import static com.gooddata.Validate.notNull;

public abstract class AbstractGoodDataCommand implements CommandMarker {

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
}
