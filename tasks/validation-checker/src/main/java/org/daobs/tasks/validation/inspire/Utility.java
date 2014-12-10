package org.daobs.tasks.validation.inspire;

import org.apache.camel.Header;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by francois on 10/12/14.
 */
public class Utility {
    public String encrypt(@Header("stringToEncrypt") String stringToEncrypt) {
        return DigestUtils.sha256Hex(stringToEncrypt);
    }
}
