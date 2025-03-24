package org.itmo.testing.lab2;

import java.io.IOException;

import org.itmo.testing.lab2.controller.UserAnalyticsController;

public class Main {
    private static final int port = 7000;

    public static void main(String[] args) throws IOException {
        final var app = UserAnalyticsController.createApp();
        app.start(port);
        System.out.println("press enter to stop...");
        System.in.read();
        app.stop();
    }
}
