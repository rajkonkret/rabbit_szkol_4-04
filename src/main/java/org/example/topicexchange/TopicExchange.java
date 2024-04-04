package org.example.topicexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicExchange {
    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeDeclare("my-topic-exchange", BuiltinExchangeType.TOPIC, true);
        channel.close();
    }


    public static void declareQueue() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // tworzymy kolejki
        channel.queueDeclare("HealthQ", true, false, false, null);
        channel.queueDeclare("SportsQ", true, false, false, null);
        channel.queueDeclare("EducationQ", true, false, false, null);

        channel.close();

    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // tworzenie linków do kolejek (routing pattern)
        channel.queueBind("HealthQ", "my-topic-exchange", "health.*");
        channel.queueBind("SportsQ", "my-topic-exchange", "#.sports.*");
        channel.queueBind("EducationQ", "my-topic-exchange", "#.education");

        channel.close();
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Drink a lot of Water and stay Healthy";
        channel.basicPublish("my-topic-exchange", "health.education", null, message.getBytes());

        message = "Learn somthing new everyday";
        channel.basicPublish("my-topic-exchange", "education", null, message.getBytes());

        message = "Stay fit in Mind and Body";
        channel.basicPublish("my-topic-exchange", "education.health", null, message.getBytes());

        message = "Just do it";
        channel.basicPublish("my-topic-exchange", "sports.sports", null, message.getBytes());

        channel.close();

    }

    public static void main(String[] args) throws IOException, TimeoutException {
        TopicExchange.declareQueue();
        TopicExchange.declareExchange();
        TopicExchange.declareBindings();

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    TopicExchange.publishMessage();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };
        publish.start();
    }


}