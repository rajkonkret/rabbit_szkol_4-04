package org.example.dirextexchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.CommonConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestPublisher {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(CommonConfig.AMQP_URL);
        Channel channel = connection.createChannel();

//        String message = "Turn on home appliances";
        String message = "Turn on personal";
//        channel.basicPublish("my-direct-exchange", "homeAppliance",null,  message.getBytes());
        channel.basicPublish("my-direct-exchange", "personalDevice",null,  message.getBytes());

        channel.close();
        connection.close();
    }
}
