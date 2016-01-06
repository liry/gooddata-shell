package cz.geek.gooddata.shell.components;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PairConverterTest {

    private final PairConverter converter = new PairConverter();

    @Test
    public void shouldConvertSingleValue() throws Exception {
        final Pair<String, String> pair = converter.convertFromText("name=value", Pair.class, null);
        assertThat(pair.getKey(), is("name"));
        assertThat(pair.getValue(), is("value"));
    }

    @Test
    public void shouldConvertMultipleValues() throws Exception {
        final Pair<String, String> pair = converter.convertFromText("n1=v1=v2", Pair.class, null);
        assertThat(pair.getKey(), is("n1"));
        assertThat(pair.getValue(), is("v1=v2"));
    }
}