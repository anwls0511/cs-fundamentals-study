package com.mujin.cs.eventloop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleEventLoopExample {

    public static void main(String[] args) throws InterruptedException {
        SimpleEventLoop eventLoop = new SimpleEventLoop();
        Thread eventLoopThread = new Thread(eventLoop::run, "simple-event-loop");

        eventLoopThread.start();

        eventLoop.submit("ACCEPT");
        eventLoop.submit("READ");
        eventLoop.submit("WRITE");
        eventLoop.submit("CLOSE");
        eventLoop.stop();

        eventLoopThread.join();
    }

    private static class SimpleEventLoop {

        private final BlockingQueue<String> events = new LinkedBlockingQueue<>();

        void submit(String event) {
            events.add(event);
        }

        void stop() {
            events.add("STOP");
        }

        void run() {
            try {
                while (true) {
                    String event = events.take();

                    if ("STOP".equals(event)) {
                        break;
                    }

                    System.out.println("event-loop: handle " + event);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("event loop interrupted", e);
            }
        }
    }
}
