package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        client.start("localhost",5005);
    }
}