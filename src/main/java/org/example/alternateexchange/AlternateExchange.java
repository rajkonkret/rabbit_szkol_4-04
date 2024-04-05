package org.example.alternateexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;
import org.example.exchangetoexchange.ExchangeToExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AlternateExchange {

    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        channel.exchangeDeclare("alt.fanout.exchange", BuiltinExchangeType.FANOUT, true);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("alternate-exchange", "alt.fanout.exchange");
        channel.exchangeDeclare("alt.topic.exchange", BuiltinExchangeType.TOPIC, true, false, arguments);

        channel.close();
    }

    public static void declareQueues() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueDeclare("HealthQ", true, false, false, null);
        channel.queueDeclare("SportsQ", true, false, false, null);
        channel.queueDeclare("EducationQ", true, false, false, null);

        channel.queueDeclare("FaultQ", true, false, false, null);

        channel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueBind("HealthQ", "alt.topic.exchange", "health.*");
        channel.queueBind("SportsQ", "alt.topic.exchange", "#.sports.*");
        channel.queueBind("EducationQ", "alt.topic.exchange", "#.education");

        channel.queueBind("FaultQ", "alt.fanout.exchange", "");

        channel.close();
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Stay fit in Mind and Body";
        channel.basicPublish("alt.topic.exchange", "education.health", null, message.getBytes());

        message = "Learn somthing new everyday";
        channel.basicPublish("alt.topic.exchange", "education", null, message.getBytes());

        channel.close();
    }

    public static void subscribeMessage() throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("HealthQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Health Queue ========");
            System.out.println(consumerTag);
            System.out.println("HealthQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("SportsQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Sports Queue ========");
            System.out.println(consumerTag);
            System.out.println("SportsQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("EducationQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Education Queue ========");
            System.out.println(consumerTag);
            System.out.println("EducationQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("FaultQ", true, ((consumerTag, message) -> {
            System.out.println("\n\n========= Fault Queue ========");
            System.out.println(consumerTag);
            System.out.println("FaultQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        AlternateExchange.declareQueues();
        AlternateExchange.declareExchange();
        AlternateExchange.declareBindings();

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    AlternateExchange.subscribeMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    AlternateExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        subscribe.start();
        publish.start();
    }
}
