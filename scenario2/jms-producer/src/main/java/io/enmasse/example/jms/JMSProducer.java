package io.enmasse.example.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicLong;

public class JMSProducer implements Runnable, ExceptionListener {

    private static final Logger log = LoggerFactory.getLogger(JMSProducer.class);
    private final ConnectionFactory connectionFactory;
    // private final Destination destination;
    private final int sleepInterval = 5000;
    private final AtomicLong counter = new AtomicLong(0);
    private final String address;

    private String MSG_SIZE = System.getenv().getOrDefault("MSG_SIZE", "1000");

    // public JMSProducer(ConnectionFactory connectionFactory, Destination destination) {
    //     this.connectionFactory = connectionFactory;
    //     this.destination = destination;
    // }

    public JMSProducer(ConnectionFactory connectionFactory, String address) {
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
            MessageProducer messageProducer = session.createProducer(q);
            while (true) {
                BytesMessage msg = session.createBytesMessage();
                msg.writeBytes(new byte[Integer.valueOf(MSG_SIZE)]);
                // TextMessage message = session.createTextMessage("JMSHello " + counter.incrementAndGet());
                messageProducer.send(msg, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
                log.info("Sent message");
                // Thread.sleep(sleepInterval);
            }

        } catch (Exception e) {
            log.warn("Received exception in producer", e);
        }
    }

    @Override
    public void onException(JMSException exception) {
        log.warn("Received JMSException", exception);
    }
}
