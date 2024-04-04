package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Main {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {

        System.out.println("Hello world!");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello, RabbitMQ";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [X] Sent'" + message + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}