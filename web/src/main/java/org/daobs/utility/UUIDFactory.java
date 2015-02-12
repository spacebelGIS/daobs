package org.daobs.utility;

import java.util.UUID;

/**
 * Created by francois on 12/02/15.
 */
public class UUIDFactory {

    public static String getNewUUID() {
        return UUID.randomUUID().toString();
    }
}
