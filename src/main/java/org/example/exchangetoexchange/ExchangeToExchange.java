package org.example.exchangetoexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ExchangeToExchange {
    public static void declareExchanges() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();
        channel.exchangeDeclare("linked-direct-exchange", BuiltinExchangeType.DIRECT, true);
        channel.exchangeDeclare("home-direct-exchange", BuiltinExchangeType.DIRECT, true);

        channel.close();
    }

    public static void declareQueues() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // tworzymy kolejki
        channel.queueDeclare("MobileQ", true, false, false, null);
        channel.queueDeclare("ACQ", true, false, false, null);
        channel.queueDeclare("LightQ", true, false, false, null);

        channel.queueDeclare("FanQ", true, false, false, null);
        channel.queueDeclare("LaptopQ", true, false, false, null);

        channel.close();

    }

    public static void declareQueueBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // tworzenie linków
        channel.queueBind("MobileQ", "linked-direct-exchange", "personalDevice");
        channel.queueBind("ACQ", "linked-direct-exchange", "homeAppliance");
        channel.queueBind("LightQ", "linked-direct-exchange", "homeAppliance");

        channel.queueBind("FanQ", "home-direct-exchange", "homeAppliance");
        channel.queueBind("LaptopQ", "home-direct-exchange", "personalDevice");

        channel.close();
    }

    public static void declareExchangeBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeBind("linked-direct-exchange", "home-direct-exchange", "homeAppliance");

        channel.close();
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

//        String message = "Direct message - Turn on the Home Appliance";
        String message = "Direct message - Turn on the personal device";
//        channel.basicPublish("home-direct-exchange", "homeAppliance", null, message.getBytes());
        channel.basicPublish("home-direct-exchange", "personalDevice", null, message.getBytes());

        channel.close();
    }

    public static void subscribeMessage() throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("MobileQ", true, ((consumerTag, message) -> {
                    System.out.println("\n\n======== Mobile Queue =========");
                    System.out.println(consumerTag);
                    System.out.println("MobileQ: " + new String(message.getBody()));
                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });

        channel.basicConsume("ACQ", true, ((consumerTag, message) -> {
                    System.out.println("\n\n======== ACQ Queue =========");
                    System.out.println(consumerTag);
                    System.out.println("ACQ: " + new String(message.getBody()));
                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });

        channel.basicConsume("LightQ", true, ((consumerTag, message) -> {
                    System.out.println("\n\n======== Light Queue =========");
                    System.out.println(consumerTag);
                    System.out.println("LightQ: " + new String(message.getBody()));
                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });

        channel.basicConsume("LaptopQ", true, ((consumerTag, message) -> {
                    System.out.println("\n\n======== Laptop Queue =========");
                    System.out.println(consumerTag);
                    System.out.println("LaptopQ: " + new String(message.getBody()));
                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });


        channel.basicConsume("FanQ", true, ((consumerTag, message) -> {
                    System.out.println("\n\n======== Fan Queue =========");
                    System.out.println(consumerTag);
                    System.out.println("FanQ: " + new String(message.getBody()));
                    System.out.println(message.getEnvelope());
                }),
                consumerTag -> {
                    System.out.println(consumerTag);
                });
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ExchangeToExchange.declareQueues();
        ExchangeToExchange.declareExchanges();
        ExchangeToExchange.declareQueueBindings();
        ExchangeToExchange.declareExchangeBindings();

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    ExchangeToExchange.subscribeMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    ExchangeToExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        subscribe.start();
        publish.start();

    }
}
