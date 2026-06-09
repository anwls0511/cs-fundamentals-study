package com.mujin.cs.network;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LengthHeaderExample {

    public static void main(String[] args) {
        String body = "{\"deviceId\":\"device-1\",\"temperature\":25.1}";
        byte[] frame = encodeFrame(body);

        System.out.println("body: " + body);
        System.out.println("frame hex: " + toHex(frame));
        System.out.println();

        String decodedBody = decodeFrame(frame);

        System.out.println("decoded body: " + decodedBody);
    }

    private static byte[] encodeFrame(String body) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(4 + bodyBytes.length);
        buffer.putInt(bodyBytes.length);
        buffer.put(bodyBytes);

        return buffer.array();
    }

    private static String decodeFrame(byte[] frame) {
        ByteBuffer buffer = ByteBuffer.wrap(frame);

        int bodyLength = buffer.getInt();
        byte[] bodyBytes = new byte[bodyLength];
        buffer.get(bodyBytes);

        System.out.println("header body length: " + bodyLength);

        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(String.format("%02X ", b));
        }

        return builder.toString().trim();
    }
}
