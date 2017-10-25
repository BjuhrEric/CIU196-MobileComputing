package com.ciu196.mobilecomputing.common.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public final class GlobalLog extends Logger {

    private static final GlobalLog logger = new GlobalLog();
    private final StreamHandler handler;


    private GlobalLog() {
        super("ServerGlobal", null);
        setLevel(Level.ALL);

        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tc] %5$s%n");
        handler = new StreamHandler(System.out, new SimpleFormatter());
        handler.setLevel(Level.ALL);
        addHandler(handler);
    }

    @Override
    public void log(LogRecord record) {
        super.log(record);
        handler.flush();
    }

    public static void log(final String string) {
        logger.log(Level.ALL, string);
    }

    public static void log(final Throwable throwable) {
        logger.log(Level.SEVERE, throwable.getMessage(), throwable);
    }

}
