package org.daobs.tasks.validation.etf;

/**
 * Class to identify the service protocol.
 *
 * @author Jose García
 *
 */
public class ServiceProtocolChecker {
    public enum ServiceProtocol {
        WMS ("WMS"),
        WMTS ("WMTS"),
        WFS ("WFS"),
        ATOM ("ATOM");

        private final String value;

        private ServiceProtocol(String s) {
            value = s;
        }

        public String toString(){
            return value;
        }

    }

    private String endPoint;

    public ServiceProtocolChecker(String endPoint) {
        this.endPoint = endPoint;
    }

    public ServiceProtocol check() {
        // TODO: Implement
        return null;
    }
}
