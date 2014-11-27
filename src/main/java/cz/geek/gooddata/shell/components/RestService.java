package cz.geek.gooddata.shell.components;

import com.gooddata.AbstractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.gooddata.util.Validate.notNull;

public class RestService extends AbstractService {

    public RestService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    public String get(final String uri) {
        notNull(uri, "uri");
        final ResponseEntity<String> entity = restTemplate.getForEntity(uri, String.class);
        return entity.getBody() != null ? entity.getBody() : "Status: " + entity.getStatusCode();
    }
}
