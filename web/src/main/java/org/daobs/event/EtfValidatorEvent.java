package org.daobs.event;

import org.springframework.context.ApplicationEvent;

/**
 * JMS event to trigger the ETF Validator.
 *
 * @author Jose García
 */
public class EtfValidatorEvent extends ApplicationEvent {
    public EtfValidatorEvent(Object source) {
        super(source);
    }
}