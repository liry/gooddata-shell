package cz.geek.gooddata.shell.commands;

import com.gooddata.project.Project;
import cz.geek.gooddata.shell.AbstractShellIntegrationTest;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.core.CommandResult;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

public class MaqlCommandTest extends AbstractShellIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Mock
    Project project;

    @Test
    public void maqlWithoutProjectToUse() {
        final CommandResult commandResult = getShell().executeCommand("maql ''");

        assertThat(commandResult.isSuccess(), is(false));
    }

    @Test
    public void maqlWithNonexistentFile() {
        final GoodDataHolder holder = getContext().getBean(GoodDataHolder.class);
        holder.setCurrentProject(project);

        final CommandResult commandResult = getShell().executeCommand("maql --file nonexistent");

        final Throwable exception = commandResult.getException();
        assertThat(exception, is(instanceOf(IllegalArgumentException.class)));
        assertThat(exception.getMessage(), matchesPattern("file .* doesn't exist"));
    }

    @Test
    public void maqlWithEmptyFile() {
        final GoodDataHolder holder = getContext().getBean(GoodDataHolder.class);
        holder.setCurrentProject(project);
        final String emptyFilePath = requireNonNull(getClass().getClassLoader().getResource("empty.file")).getPath();

        logger.info("maqlWithEmptyFile test is about to run: maql --file {}", emptyFilePath);
        final CommandResult commandResult = getShell().executeCommand("maql --file " + emptyFilePath);

        final Throwable exception = commandResult.getException();
        assertThat(exception, is(instanceOf(IllegalArgumentException.class)));
        assertThat(exception.getMessage(), is("maql file cannot be empty"));
    }
}