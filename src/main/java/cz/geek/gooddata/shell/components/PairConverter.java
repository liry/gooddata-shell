package cz.geek.gooddata.shell.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class PairConverter implements Converter<Pair<String, String>> {

    @Override
    public boolean supports(final Class<?> type, final String optionContext) {
        return Pair.class.isAssignableFrom(type);
    }

    @Override
    public Pair<String, String> convertFromText(final String value, final Class<?> targetType, final String optionContext) {
        if (!supports(targetType, optionContext)) {
            return null;
        }
        final String[] split = StringUtils.split(value, "=", 2);

        return new ImmutablePair<>(split[0], split[1]);
    }

    @Override
    public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> targetType, final String existingData, final String optionContext, final MethodTarget target) {
        return false;
    }
}
