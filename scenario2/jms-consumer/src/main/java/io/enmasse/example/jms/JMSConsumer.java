package io.enmasse.example.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class JMSConsumer implements Runnable, ExceptionListener {
    private static final Logger log = LoggerFactory.getLogger(JMSConsumer.class);
    private final ConnectionFactory connectionFactory;
    // private final Destination destination;
    private String address;

    // public JMSConsumer(ConnectionFactory connectionFactory, Destination destination) {
    //     this.connectionFactory = connectionFactory;
    //     this.destination = destination;
    // }

    public JMSConsumer(ConnectionFactory connectionFactory, String address) {
            this.connectionFactory = connectionFactory;
            this.address = address;
        }

    @Override
    public void run() {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.setExceptionListener(this);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue q = session.createQueue(address);
            MessageConsumer messageConsumer = session.createConsumer(q);

            while (true) {
                BytesMessage message = (BytesMessage) messageConsumer.receive();

                log.info("Received '{}'", message.getBodyLength());
            }
        } catch (Exception e) {
            log.warn("Received exception in consumer", e);
        }
    }

    @Override
    public void onException(JMSException exception) {
        log.warn("Received JMSException", exception);
    }
}
