package org.daobs.event;

import org.springframework.context.ApplicationEvent;

/**
 * JMS event to trigger the ETF Validator.
 *
 * @author Jose García
 */
public class EtfValidatorEvent extends ApplicationEvent {

    final String fq;

    public String getFq() {
        return fq;
    }

    public EtfValidatorEvent(Object source, final String fq) {
        super(source);
        this.fq = fq;
    }
}