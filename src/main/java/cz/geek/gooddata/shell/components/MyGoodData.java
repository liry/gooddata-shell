package cz.geek.gooddata.shell.components;

import com.gooddata.GoodData;
import com.gooddata.GoodDataSettings;

public class MyGoodData extends GoodData {

    private final RestService service;

    public MyGoodData(final Credentials credentials) {
        super(credentials.getHost(), credentials.getUser(), credentials.getPass(), credentials.getPort(),
                credentials.getProtocol(), new GoodDataSettings());
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
        private final int port;
        private final String protocol;

        public Credentials(final String host, final String user, final String pass) {
            this(host, user, pass, 443, "https");
        }

        public Credentials(final String host, final String user, final String pass, final int port,
                           final String protocol) {
            this.host = host;
            this.user = user;
            this.pass = pass;
            this.port = port;
            this.protocol = protocol;
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

        public int getPort() {
            return port;
        }

        public String getProtocol() {
            return protocol;
        }
    }
}
