package org.example.dirextexchange;

import com.rabbitmq.client.*;
import org.example.CommonConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(CommonConfig.AMQP_URL);
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(consumerTag);
            System.out.println(new String(message.getBody(), "UTF-8"));
        };

        CancelCallback cancelCallback = consumerTag -> {
            System.out.println(consumerTag);
        };
//        channel.basicConsume("ACQ", true, deliverCallback, cancelCallback);
//        channel.basicConsume("LightQ", true, deliverCallback, cancelCallback);
        channel.basicConsume("MobileQ", true, deliverCallback, cancelCallback);
    }
}
