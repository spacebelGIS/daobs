package org.daobs.tasks.validation.etf;

/**
 * ServiceType enumeration.
 *
 * @author Jose García
 */
public enum ServiceType {
    View ("view"),
    Download ("download");

    private final String value;

    ServiceType(String s) {
        value = s;
    }

    public String toString(){
        return value;
    }

    public static ServiceType fromString(String text) {
        if (text != null) {
            for (ServiceType s : ServiceType.values()) {
                if (text.equalsIgnoreCase(s.value)) {
                    return s;
                }
            }
        }

        throw new IllegalArgumentException("No ServiceType with value " + text + " found.");

    }
}
