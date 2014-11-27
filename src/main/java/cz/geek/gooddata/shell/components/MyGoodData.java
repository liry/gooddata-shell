package cz.geek.gooddata.shell.components;

import com.gooddata.GoodData;

public class MyGoodData extends GoodData {

    private final RestService service;

    public MyGoodData(final String hostname, final String login, final String password) {
        super(hostname, login, password);
        service = new RestService(getRestTemplate());
    }

    public MyGoodData(final String login, final String password) {
        super(login, password);
        service = new RestService(getRestTemplate());
    }

    public RestService getRestService() {
        return service;
    }
}
