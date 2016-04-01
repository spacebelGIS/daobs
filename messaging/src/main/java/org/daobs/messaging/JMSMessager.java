/**
 * Copyright 2014-2016 European Environment Agency
 *
 * Licensed under the EUPL, Version 1.1 or – as soon
 * they will be approved by the European Commission -
 * subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package org.daobs.messaging;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * Created by francois on 05/11/15.
 */
public class JMSMessager {
    private Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private PooledConnectionFactory connectionFactory;

    @Autowired
    private JmsTemplate template;

    public JMSMessager() {
    }

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
