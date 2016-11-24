package cz.geek.gooddata.shell.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cz.geek.gooddata.shell.components.GoodDataHolder;
import cz.geek.gooddata.shell.components.RestService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class RestCommand extends AbstractGoodDataCommand {

    private static final List<MediaType> JSON_TYPE = asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Autowired
    public RestCommand(final GoodDataHolder holder) {
        super(holder);
    }

    @CliAvailabilityIndicator({"rest get"})
    public boolean isAvailable() {
        return holder.hasGoodData();
    }

    @CliCommand(value = "rest get", help = "Perform GET request to the given URI")
    public String get(@CliOption(key = {"uri", ""}, mandatory = true) String uri,
                      @CliOption(key = "target") File target,
                      @CliOption(key = "raw", help = "Raw value (don't pretty print JSON)",
                              unspecifiedDefaultValue = "false", specifiedDefaultValue = "true") boolean raw)
            throws IOException {
        final RestService service = getGoodData().getRestService();

        if (target != null) {
            final ResponseEntity<byte[]> response = service.get(uri, byte[].class);
            try (OutputStream output = new FileOutputStream(target)) {
                final int length = IOUtils.copy(new ByteArrayInputStream(response.getBody()), output);
                return "Status: " + response.getStatusCode() + " " + target.getAbsolutePath() + ": " + length + " bytes";
            }
        }

        final ResponseEntity<String> response = service.get(uri, String.class);
        if (response.getBody() == null) {
            return "Status: " + response.getStatusCode();
        } else if (!raw && isCompatible(response.getHeaders())) {
            final JsonNode tree = mapper.readTree(response.getBody());
            return mapper.writeValueAsString(tree);
        } else {
            return response.getBody();
        }
    }

    private static boolean isCompatible(final HttpHeaders headers) {
        for (MediaType mediaType: JSON_TYPE) {
            if (mediaType.isCompatibleWith(headers.getContentType())) {
                return true;
            }
        }
        return false;
    }

}
