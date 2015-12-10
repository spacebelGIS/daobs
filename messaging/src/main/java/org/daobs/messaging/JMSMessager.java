package org.daobs.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;

import javax.jms.*;

/**
 * Created by francois on 05/11/15.
 */
public class JMSMessager {
    private Log log = LogFactory.getLog(this.getClass());


    private String jmsUrl;

    public String getJmsUrl() {
        return jmsUrl;
    }

    public void setJmsUrl(String jmsUrl) {
        this.jmsUrl = jmsUrl;
    }

    public void sendMessage(String queue, ApplicationEvent event) {
        Connection connection = null;
        Session session = null;
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.jmsUrl);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(queue);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            ObjectMessage message = session.createObjectMessage(event);

            // Tell the producer to send the message
            producer.send(message);
            log.info("JMSMessanger - message send (" + message.toString() + ") to queue: " + queue);

        } catch (Exception e) {
            log.error("JMSMessanger", e);
            e.printStackTrace();
        } finally {
            // Clean up
            try {
                if (session != null) session.close();
                if (connection != null) connection.close();

            } catch (JMSException e) {
                log.error("JMSMessanger", e);
                e.printStackTrace();
            }
        }
    }
}
