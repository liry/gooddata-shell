package cz.geek.gooddata.shell.components;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.HistoryFileNameProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GoodDataHistoryFileNameProvider implements HistoryFileNameProvider {

    private static final Set<PosixFilePermission> PERMS = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);

    public String getHistoryFileName() {
        final Path dir = Paths.get(System.getProperty("user.home"), ".gooddatashell");
        if (!dir.toFile().exists()) {
            try {
                if (isWindows()) {
                    Files.createDirectories(dir);
                } else {
                    Files.createDirectories(dir, PosixFilePermissions.asFileAttribute(PERMS));
                }
            } catch (IOException ignored) {
            }
        }
        return dir.resolve("history").toString();
    }

    public String getProviderName() {
        return "gooddata-history-provider";
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }
}