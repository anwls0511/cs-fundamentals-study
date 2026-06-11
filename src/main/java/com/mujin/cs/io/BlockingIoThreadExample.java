package com.mujin.cs.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class BlockingIoThreadExample {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 19110;

    public static void main(String[] args) throws Exception {
        CountDownLatch serverReady = new CountDownLatch(1);

        Thread serverThread = new Thread(() -> runServer(serverReady), "blocking-server-thread");
        serverThread.start();

        serverReady.await();
        runClient();

        serverThread.join();
    }

    private static void runServer(CountDownLatch serverReady) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("server: listen " + HOST + ":" + PORT);
            serverReady.countDown();

            try (Socket socket = serverSocket.accept();
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

                System.out.println("server: waiting for client message on " + Thread.currentThread().getName());

                String request = reader.readLine();
                System.out.println("server: read " + request);

                writer.println("PONG");
            }
        } catch (IOException e) {
            throw new IllegalStateException("server failed", e);
        }
    }

    private static void runClient() throws IOException, InterruptedException {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            System.out.println("client: sleep before sending message");
            Thread.sleep(1000);

            writer.println("PING");
            System.out.println("client: send PING");

            String response = reader.readLine();
            System.out.println("client: read " + response);
        }
    }
}
