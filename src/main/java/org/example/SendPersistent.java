package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;

public class SendPersistent {

    private final static String QUEUE_NAME = "persit_queue";

    public static void main(String[] args) throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

        String message = "Persist Message";
        channel.basicPublish("",
                QUEUE_NAME,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes("UTF-8"));

        System.out.println("Wysłano: " + message);
    }
}
