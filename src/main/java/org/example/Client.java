package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    public void start(String address, int port) {
        try {
            ConnectionThread thread = new ConnectionThread(address, port);
            thread.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Proszę podać nazwę użytkownika: ");
            String username = reader.readLine();
            thread.login(username);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    thread.disconnect(username);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }));

            String rawMessage;

            while((rawMessage = reader.readLine()) != null) {
                if (rawMessage.equals("/online")) {
                    thread.getUserList();
                } else if (rawMessage.startsWith("/w")) {
                    int recipientLength = rawMessage.indexOf(' ', 3);
                    String recipient = rawMessage.substring(3, recipientLength);
                    String messageContent = rawMessage.substring(recipientLength + 1);
                    Message message = new Message(Message.MessageType.DM, messageContent, recipient, username);
                    thread.send(message);
                } else {
                    Message message = new Message(Message.MessageType.Broadcast, rawMessage, username);
                    thread.send(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
