package cz.geek.gooddata.shell.components;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.HistoryFileNameProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.*;
import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static java.util.Arrays.asList;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GoodDataHistoryFileNameProvider implements HistoryFileNameProvider {

    private static final FileAttribute<Set<PosixFilePermission>> ATTRS = asFileAttribute(new HashSet<>(asList(
                    OWNER_READ, OWNER_WRITE, OWNER_EXECUTE)));

    public String getHistoryFileName() {
        final Path dir = Paths.get(System.getProperty("user.home"), ".gooddatashell");
        try {
            Files.createDirectories(dir, ATTRS);
        } catch (IOException ignored) {
        }
        return dir.resolve("history").toString();
	}

	public String getProviderName() {
		return "gooddata-history-provider";
	}
}