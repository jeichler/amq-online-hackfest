package io.enmasse.example.jms;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String RATE = System.getenv().getOrDefault("RATE", "10");
    private static final Integer PRODUCERS_NUMBER = Integer.valueOf(System.getenv().getOrDefault("PRODUCERS_NUMBER", "5"));

    public static void main(String [] args) throws Exception {
        AppConfiguration appConfiguration = AppConfiguration.create(System.getenv());

        Map<String, Object> groupconnection = new HashMap<>();
        groupconnection.put("host", appConfiguration.getHostname());
        groupconnection.put("port", appConfiguration.getPort());

        // JmsConnectionFactory connectionFactory = new JmsConnectionFactory();
        
        TransportConfiguration tcf = new TransportConfiguration(NettyConnectorFactory.class.getName(), groupconnection);

       ActiveMQConnectionFactory connectionFactory =  ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.QUEUE_CF, tcf);

        // connectionFactory.setRemoteURI(String.format("amqp://%s:%d", appConfiguration.getHostname(), appConfiguration.getPort()));
        connectionFactory.setUser(appConfiguration.getUsername());
        connectionFactory.setPassword(appConfiguration.getPassword());
        connectionFactory.setProducerMaxRate(Integer.valueOf(RATE));
        
        // Queue destination = new JmsQueue(appConfiguration.getAddress());
        // destination.setAddress(appConfiguration.getAddress());

        Executor executor = Executors.newFixedThreadPool(PRODUCERS_NUMBER);

        // JMSProducer producer = new JMSProducer(connectionFactory, appConfiguration.getAddress());
        JMSProducer producer = null;

        for (int i = 0; i < PRODUCERS_NUMBER;i++){

            producer = new JMSProducer(connectionFactory, appConfiguration.getAddress());

            executor.execute(producer);
 
        }
    }
}
