package org.daobs.tasks.validation.etf;

/**
 * ServiceProtocol enumeration.
 *
 * @author Jose García
 */
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
