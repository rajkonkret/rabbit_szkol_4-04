package org.example;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;

public class CheckQueueExists {
    public static void main(String[] args) throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

//        String queueName = "hello";
        String queueName = "FanQ";
        try {
            // pasywne deklarowanie kolejki
            AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive(queueName);
            System.out.println("Kolejka istnieje");
            System.out.println("Liczba wiadomości: " + queueName + " : " + declareOk.getMessageCount());
        } catch (IOException e) {
            System.out.println("Kolejka nie istnieje");
        }
    }
}