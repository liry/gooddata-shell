package cz.geek.gooddata.shell.components;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GoodDataBannerProvider implements BannerProvider {

    private static final ClassPathResource BANNER = new ClassPathResource("banner.txt");

    @Override
    public String getBanner() {
        try {
            return StreamUtils.copyToString(BANNER.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public String getVersion() {
        return "TODO"; // todo
    }

    @Override
    public String getWelcomeMessage() {
        return "Welcome to GoodData Shell. For assistance type \"help\" then hit ENTER.";
    }

    @Override
    public String getProviderName() {
        return "GoodData Shell";
    }
}
