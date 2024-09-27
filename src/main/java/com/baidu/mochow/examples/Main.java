package com.baidu.mochow.examples;

import com.baidu.mochow.auth.Credentials;
import com.baidu.mochow.client.ClientConfiguration;

public class Main {
    public static void main(String[] args) {
        System.out.println("Begin to execute mochow example");
        String account = "root";
        String apiKey = "*********";
        String endpoint = "*.*.*.*:*"; // example: 127.0.0.1:5287
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setCredentials(new Credentials(account, apiKey));
        clientConfiguration.setEndpoint(endpoint);
        MochowExample example = new MochowExample(clientConfiguration);
        example.example();
        System.out.println("Finish to execute mochow example");
        System.exit(0);
    }
}