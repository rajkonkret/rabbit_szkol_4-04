package org.example.fanoutexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FanoutExchange {
    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeDeclare("my-fanout-exchange", BuiltinExchangeType.FANOUT, true);
        channel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueBind("MobileQ", "my-fanout-exchange", "");
        channel.queueBind("ACQ", "my-fanout-exchange", "");
        channel.queueBind("LightQ", "my-fanout-exchange", "");

        channel.close();
    }

    public static void subscribeMessages() throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("LightQ", true, (
                        (consumerTag, message) -> {
                            System.out.println(consumerTag);
                            System.out.println("LightQ" + new String(message.getBody()));
                        }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });

        channel.basicConsume("ACQ", true, (
                        (consumerTag, message) -> {
                            System.out.println(consumerTag);
                            System.out.println("ACQ" + new String(message.getBody()));
                        }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });

        channel.basicConsume("MobileQ", true, (
                        (consumerTag, message) -> {
                            System.out.println(consumerTag);
                            System.out.println("MobileQ" + new String(message.getBody()));
                        }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });
    }
}
