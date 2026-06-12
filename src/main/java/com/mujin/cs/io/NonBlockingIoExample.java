package com.mujin.cs.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class NonBlockingIoExample {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 19120;

    public static void main(String[] args) throws Exception {
        CountDownLatch serverReady = new CountDownLatch(1);

        Thread serverThread = new Thread(() -> runServer(serverReady), "non-blocking-server-thread");
        serverThread.start();

        serverReady.await();
        runClient();

        serverThread.join();
    }

    private static void runServer(CountDownLatch serverReady) {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(HOST, PORT));
            System.out.println("server: listen " + HOST + ":" + PORT);
            serverReady.countDown();

            try (SocketChannel clientChannel = serverChannel.accept()) {
                clientChannel.configureBlocking(false);

                ByteBuffer buffer = ByteBuffer.allocate(128);
                int firstRead = clientChannel.read(buffer);

                if (firstRead == 0) {
                    System.out.println("non-blocking read: no data yet");
                }

                int readBytes = 0;

                while (readBytes == 0) {
                    buffer.clear();
                    readBytes = clientChannel.read(buffer);
                    Thread.sleep(100);
                }

                buffer.flip();
                String message = StandardCharsets.UTF_8.decode(buffer).toString().trim();
                System.out.println("server: read " + message);
            }
        } catch (IOException e) {
            throw new IllegalStateException("server failed", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("server interrupted", e);
        }
    }

    private static void runClient() throws IOException, InterruptedException {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            Thread.sleep(500);

            writer.println("PING");
            System.out.println("client: send PING");
        }
    }
}
