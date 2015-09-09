package cz.geek.gooddata.shell.components;

import com.gooddata.GoodData;

public class MyGoodData extends GoodData {

    private final RestService service;

    public MyGoodData(final Credentials credentials) {
        super(credentials.getHost(), credentials.getUser(), credentials.getPass());
        service = new RestService(getRestTemplate());
    }

    public RestService getRestService() {
        return service;
    }

    // this must be an inner class, because the HOSTNAME constant has protected access
    public static class Credentials {
        private final String host;
        private final String user;
        private final String pass;

        public Credentials(final String host, final String user, final String pass) {
            this.host = host;
            this.user = user;
            this.pass = pass;
        }

        public String getHost() {
            return host == null ? HOSTNAME : host;
        }

        public String getUser() {
            return user;
        }

        public String getPass() {
            return pass;
        }
    }
}
