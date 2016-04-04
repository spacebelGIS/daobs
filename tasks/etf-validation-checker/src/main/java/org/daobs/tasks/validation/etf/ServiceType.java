/**
 * Copyright 2014-2016 European Environment Agency <p> Licensed under the EUPL, Version 1.1 or – as
 * soon they will be approved by the European Commission - subsequent versions of the EUPL (the
 * "Licence"); You may not use this work except in compliance with the Licence. You may obtain a
 * copy of the Licence at: <p> https://joinup.ec.europa.eu/community/eupl/og_page/eupl <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */

package org.daobs.tasks.validation.etf;

/**
 * ServiceType enumeration.
 *
 * @author Jose García
 */
public enum ServiceType {
  View("view"),
  Download("download");

  private final String value;

  ServiceType(String serviceType) {
    value = serviceType;
  }

  /**
   * Parse service type.
     */
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

  public String toString() {
    return value;
  }
}
