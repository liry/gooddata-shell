package cz.geek.gooddata.shell.commands;

import cz.geek.gooddata.shell.components.GoodDataHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 */
@Component
public class StorageCommand extends AbstractGoodDataCommand {

    @Autowired
    public StorageCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"storage upload", "storage download"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }


    @CliCommand(value = "storage upload", help = "Upload file to the user staging area (WebDAV)")
    public String upload(
            @CliOption(key = {"src", ""}, mandatory = true, help = "Source file on local disk") File src,
            @CliOption(key = {"dst"}, mandatory = false, help = "Destination file name") String dst) throws FileNotFoundException {

        final String name = dst != null ? dst : src.getName();
        getGoodData().getDataStoreService().upload(name, new FileInputStream(src));
        return "uploaded to " + name;
    }

    @CliCommand(value = "storage download", help = "Download file from the user staging area (WebDAV)")
    public String download(
            @CliOption(key = {"src", ""}, mandatory = true, help = "Source file on staging area") String src,
            @CliOption(key = {"dst"}, mandatory = false, help = "Destination file name") File dst) throws IOException {

        final File name = dst != null ? dst : new File(src);
        final InputStream stream = getGoodData().getDataStoreService().download(src);
        StreamUtils.copy(stream, new FileOutputStream(name));
        return "downloaded to " + name;
    }
}
