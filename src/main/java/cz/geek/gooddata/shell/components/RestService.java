package cz.geek.gooddata.shell.components;

import com.gooddata.AbstractService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.gooddata.util.Validate.notNull;

public class RestService extends AbstractService {

    public RestService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    public <T> ResponseEntity<T> get(final String uri, Class<T> cls) {
        notNull(uri, "uri");
        return restTemplate.getForEntity(uri, cls);
    }

    public <T> ResponseEntity<T> post(final String uri, final Object request, final Class<T> cls) {
        notNull(uri, "uri");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<?> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(uri, HttpMethod.POST, entity, cls);
    }
}
