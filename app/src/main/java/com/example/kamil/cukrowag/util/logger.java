package com.example.kamil.cukrowag.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kamil on 10.06.17.
 */
public class logger {
    private static int msgnum = 0;
    private static String logs = new String();
    private static Consumer0 loggerRefresher = null;

    public static String log_do(final String log) {
        String newline = "#" + ++msgnum + " " + System.currentTimeMillis() + "[" + logger.getMethodName(3) + "]: " +
                log + ( log.length() > 0 && log.charAt(log.length() - 1) == '\n' ? "" : "\n" );
        logs += newline;
        Log.i("CukroWag", newline);
        if ( loggerRefresher != null ) {
            loggerRefresher.accept();
        }
        return newline;
    }

    public static void l(Context context, String log) {
        logger.log_do(log);
    }
    public static void l(String log) {
        logger.log_do(log);
    }
    public static void l() {
        logger.log_do(new String());
    }
    public static void t(Context context, String log) {
        logger.log_do(log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
    }
    public static void t(Context context, String log, int duration) {
        logger.log_do(log);
        Toast.makeText(context, log, duration).show();
    }
    public static void a(Context context, String string) {
        String newline = log_do(string);
        new AlertDialog.Builder(context)
                .setMessage("Alert " + newline)
                .setTitle("App Title")
                .setPositiveButton("OK", null)
                .setCancelable(true)
                .create().show();
    }

    public static String getLogs() {
        return logs;
    }

    /**
     * Get the method name for a depth in call stack. <br />
     * Utility function
     * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
     * @return method name
     */
    public static String getMethodName(final int depth)
    {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        if ( trace.length < depth ) {
            throw new RuntimeException("very very bad");
        }

        return trace[depth].getClassName() + "."
                        + trace[depth].getMethodName() + ":"
                        + trace[depth].getLineNumber();
    }

    public static void setLoggerRefresher(Consumer0 loggerRefresher) {
        logger.loggerRefresher = loggerRefresher;
    }
}
