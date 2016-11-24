package cz.geek.gooddata.shell.components;

import com.gooddata.AbstractService;
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
}
