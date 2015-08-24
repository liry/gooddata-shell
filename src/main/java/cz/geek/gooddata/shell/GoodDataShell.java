package cz.geek.gooddata.shell;

import org.springframework.shell.Bootstrap;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.write;
import static org.apache.commons.lang.StringUtils.join;

public class GoodDataShell extends Bootstrap {

    public static void main(String[] args) throws IOException {

        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        final File file = writeCommandsToTmpFile(args);
        final String[] arguments = {"--cmdfile", file.getAbsolutePath()};

        Bootstrap.main(arguments);
    }

    private static File writeCommandsToTmpFile(final String[] args) throws IOException {
        final File file = File.createTempFile("ads", ".txt");
        file.deleteOnExit();
        final String commands = join(args, " ");
        write(file, commands);
        return file;
    }

}
