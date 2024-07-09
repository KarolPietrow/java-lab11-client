package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class ConnectionThread extends Thread{
    Socket socket;
    PrintWriter writer;

    public ConnectionThread(String address, int port) throws IOException {
        socket = new Socket(address, port);
    }

    public void run(){
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            writer = new PrintWriter(output, true);
            String rawMessage;
            while((rawMessage = reader.readLine()) != null) {
                Message message = new ObjectMapper().readValue(rawMessage, Message.class);
                String formattedMessage = (message.username + ": " + message.content);
                switch (message.type) {
                    case Broadcast -> System.out.println(formattedMessage);
                    case DM -> System.out.println(message.username + " (WIADOMOŚĆ PRYWATNA): " + message.content);
                    case Login -> System.out.println(message.content);
                    case Disconnect -> System.out.println("Użytkownik " + message.content + " opuścił czat.");
                    case UserList -> System.out.println("Lista aktywnych użytkowników na czacie: " + message.content);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try { socket.close(); } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send (Message message) throws JsonProcessingException {
        String rawMessage = new ObjectMapper().writeValueAsString(message);
        writer.println(rawMessage);
    }

    public void login (String username) throws JsonProcessingException {
        Message message = new Message(Message.MessageType.Login, username);
        send(message);
    }

    public void getUserList() throws JsonProcessingException {
        send(new Message(Message.MessageType.UserList, "", "SYSTEM"));
    }

    public void disconnect(String username) throws JsonProcessingException {
        Message message = new Message(Message.MessageType.Disconnect, username);
        send(message);
    }

}
