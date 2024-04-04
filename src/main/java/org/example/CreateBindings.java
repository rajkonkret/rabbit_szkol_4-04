package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CreateBindings {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(CommonConfig.AMQP_URL);

        try (Channel channel = connection.createChannel()) {
            channel.queueBind("MobileQ", "my-direct-exchange", "personalDevice");
            channel.queueBind("ACQ", "my-direct-exchange", "homeAppliance");
            channel.queueBind("LightQ", "my-direct-exchange", "homeAppliance");
        }
        connection.close();
    }
}