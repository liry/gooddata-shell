package cz.geek.gooddata.shell.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GoodDataPromptProvider implements PromptProvider {

    private final GoodDataHolder holder;

    @Autowired
    public GoodDataPromptProvider(GoodDataHolder holder) {
        this.holder = holder;
    }

    @Override
    public String getPrompt() {
        final StringBuilder builder = new StringBuilder();
        builder.append("gdsh");
        if (holder.hasGoodData()) {
            builder.append(":").append(holder.getShortHost());
        }
        if (holder.hasCurrentProject()) {
            builder.append(":").append(holder.getCurrentProject().getId());
        }
        if (holder.hasCurrentWarehouse()) {
            builder.append(":").append(holder.getCurrentWarehouse().getWarehouse().getId());
        }
        builder.append("> ");
        return builder.toString();
    }

    @Override
    public String getProviderName() {
        return "gooddata-prompt-provider";
    }
}
