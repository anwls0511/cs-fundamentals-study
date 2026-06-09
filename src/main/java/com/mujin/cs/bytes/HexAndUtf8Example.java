package com.mujin.cs.bytes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HexAndUtf8Example {

    public static void main(String[] args) {
        printIntegerAsBytes(42);
        printUtf8Bytes("ABC");
        printUtf8Bytes("\uAC00");
        printUtf8Bytes("hello \uB124\uD2B8\uC6CC\uD06C");
    }

    private static void printIntegerAsBytes(int value) {
        byte[] bytes = ByteBuffer.allocate(4)
                .putInt(value)
                .array();

        System.out.println("int value: " + value);
        System.out.println("4-byte hex: " + toHex(bytes));
        System.out.println();
    }

    private static void printUtf8Bytes(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        System.out.println("text: " + text);
        System.out.println("char length: " + text.length());
        System.out.println("byte length: " + bytes.length);
        System.out.println("utf-8 hex: " + toHex(bytes));
        System.out.println();
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(String.format("%02X ", b));
        }

        return builder.toString().trim();
    }
}
