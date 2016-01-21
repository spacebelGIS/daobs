package org.daobs.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * Created by francois on 05/11/15.
 */
public class JMSMessager {
    private Log log = LogFactory.getLog(this.getClass());

    @Autowired
    PooledConnectionFactory connectionFactory;

    @Autowired
    JmsTemplate template;

    public void sendMessage(final String queue, final ApplicationEvent event) {
        try {
            template.send(queue, new MessageCreator() {
                public Message createMessage(Session session)
                        throws JMSException {
                    ObjectMessage message = session.createObjectMessage(event);
                    log.info("JMSMessanger - message send (" + message.toString() + ") to queue: " + queue);
                    return message;
                }
            });
        } catch (Exception e) {
            log.error(this.getClass().getSimpleName(), e);
            e.printStackTrace();
        }
    }
}
