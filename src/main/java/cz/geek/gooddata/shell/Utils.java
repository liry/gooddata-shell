package cz.geek.gooddata.shell;

import com.gooddata.md.Entry;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.apache.commons.lang.Validate.noNullElements;

/**
 */
public abstract class Utils {

    public static String entryCollectionToString(final Collection<Entry> list) {
        noNullElements(list);
        final ArrayList<Entry> entries = new ArrayList<>(list);
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(final Entry o1, final Entry o2) {
                return o1.getIdentifier().compareToIgnoreCase(o2.getIdentifier());
            }
        });
        final List<String> result = new ArrayList<>(entries.size());
        for (Entry entry: entries) {
            result.add(entry.getLink() + "\t" + entry.getIdentifier() + "\t" + entry.getTitle());
        }
        return StringUtils.collectionToDelimitedString(result, "\n");
    }
}
