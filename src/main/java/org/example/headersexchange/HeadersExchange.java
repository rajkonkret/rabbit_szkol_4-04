package org.example.headersexchange;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class HeadersExchange {

    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeDeclare("my-header-exchange", BuiltinExchangeType.HEADERS, true);
        channel.close();
    }

    public static void declareQueue() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // tworzymy kolejki
        channel.queueDeclare("HealthQ", true, false, false, null);
        channel.queueDeclare("SportsQ", true, false, false, null);
        channel.queueDeclare("EducationQ", true, false, false, null);
        channel.queueDeclare("VercomQ", true, false, false, null);

        channel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // tworzenie linków
        // {"arg":"wartość"}
        Map<String, Object> healthArgs = new HashMap<>();
        healthArgs.put("x-match", "any"); // any - dowolny
        healthArgs.put("h1", "Header1");
        healthArgs.put("h2", "Header2");
        channel.queueBind("HealthQ", "my-header-exchange", "", healthArgs);

        Map<String, Object> sportsArgs = new HashMap<>();
        sportsArgs.put("x-match", "all"); // all - czyli wszystkie
        sportsArgs.put("h1", "Header1");
        sportsArgs.put("h2", "Header2");
        channel.queueBind("SportsQ", "my-header-exchange", "", sportsArgs);

        Map<String, Object> educationArgs = new HashMap<>();
        educationArgs.put("x-match", "any");
        educationArgs.put("h1", "Header1");
        educationArgs.put("h2", "Header2");
        channel.queueBind("EducationQ", "my-header-exchange", "", educationArgs);

        Map<String, Object> vercomArgs = new HashMap<>();
        vercomArgs.put("x-match", "any");
        vercomArgs.put("h3", "Header3");
        channel.queueBind("VercomQ", "my-header-exchange", "", vercomArgs);

        channel.close();
    }

    public static void subscribeMessage() throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("HealthQ", true,
                ((consumerTag, message) -> {
                    System.out.println("\n\n============ Health Queue ===========");
                    System.out.println(consumerTag);
                    System.out.println("HealthQ: " + new String(message.getBody()));


                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });

        channel.basicConsume("SportsQ", true,
                ((consumerTag, message) -> {
                    System.out.println("\n\n======== Sports Queue =========");
                    System.out.println(consumerTag);
                    System.out.println("SportsQ" + new String(message.getBody()));

                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });

        channel.basicConsume("EducationQ", true,
                ((consumerTag, message) -> {
                    System.out.println("\n\n======== Education Queue =========");
                    System.out.println(consumerTag);
                    System.out.println("EducationQ" + new String(message.getBody()));

                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });

        channel.basicConsume("VercomQ", true,
                ((consumerTag, message) -> {
                    System.out.println("\n\n======== Vercom Queue =========");
                    System.out.println(consumerTag);
                    System.out.println("VercomQ" + new String(message.getBody()));

                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Headers exchange example 1";
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("h1", "Header1");
        headerMap.put("h2", "Header2");
        BasicProperties properties = new BasicProperties().builder()
                .headers(headerMap)
                .build();
        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());

        message = "Headers exchange example 2";
        Map<String, Object> headerMap2 = new HashMap<>();
        headerMap2.put("h2", "Header2");
        properties = new BasicProperties().builder()
                .headers(headerMap2).build();
        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());

        message = "Headers exchange example 3";
        Map<String, Object> headerMap3 = new HashMap<>();
        headerMap3.put("h3", "Header3");
        properties = new BasicProperties().builder()
                .headers(headerMap3)
                .build();
        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());

        message = "Headers exchange example 4 Vercom";
        Map<String, Object> headerMap4 = new HashMap<>();
        headerMap4.put("h3", "Header3");
        properties = new BasicProperties().builder()
                .headers(headerMap4)
                .build();
        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());

        message = "Headers exchange example 5";
        Map<String, Object> headerMap5 = new HashMap<>();
        headerMap5.put("h2", "Header2");
        properties = new BasicProperties().builder()
                .headers(headerMap5)
                .build();
        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());

        channel.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        HeadersExchange.declareQueue();
        HeadersExchange.declareExchange();
        HeadersExchange.declareBindings();

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    HeadersExchange.subscribeMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    HeadersExchange.publishMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        subscribe.start();
        publish.start();

    }
}