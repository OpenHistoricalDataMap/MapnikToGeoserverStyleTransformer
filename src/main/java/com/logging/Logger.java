package com.logging;

public class Logger {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_WHITE = "\u001B[37m";
    public static boolean debug = true;

    public static void debug(String message) {
        if (debug) log(ANSI_WHITE, message);
    }

    public static void success(String message) {
        log(ANSI_GREEN, message);
    }

    public static void warn(String message) {
        log(ANSI_YELLOW, message);
    }

    public static void error(String message) {
        log(ANSI_RED, message);
    }

    private static void log(String color, String message) {
        System.out.println(color + message + ANSI_RESET);
    }
}
