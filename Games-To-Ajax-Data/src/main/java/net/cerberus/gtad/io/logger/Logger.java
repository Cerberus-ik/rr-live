package net.cerberus.gtad.io.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static void logMessage(String message, LogLevel logLevel, LogReason logReason) {
        System.out.println(getMessagePrefix() + " [" + logLevel.getName() + "] [" + logReason.getName() + "]: " + message);
    }

    private static String getMessagePrefix() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss]");
        return simpleDateFormat.format(new Date());
    }
}
