// https://interactivebrokers.github.io/tws-api/connection.html#ereader

package net.zelcon.ibkr_api_client_wrapper;

import java.time.Duration;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.ib.client.*;
import java.io.IOException;
import java.lang.System.Logger;
import java.text.MessageFormat;

public class EReaderThread {
    private static final Duration WAIT_FOR_CONNECTION_TIMMEOUT = Duration.ofSeconds(5);

    private static final Duration STOP_THREAD_TIMEOUT = Duration.ofSeconds(10);

    private static final Logger logger = System.getLogger(EReaderThread.class.getName());

    private final EClientSocket client;
    private final EReaderSignal signal;
    private EReader ereader;
    private Thread ereaderThread = new Thread(this::processMessageQueue);

    private EReaderThread(@NonNull final EClientSocket client, @NonNull final EReaderSignal signal) {
        if (client == null || signal == null)
            throw new IllegalArgumentException("EReaderThread constructor arguments cannot be null");
        this.client = client;
        this.signal = signal;
    }

    public static EReaderThread start(@NonNull final EClientSocket client, @NonNull final EReaderSignal signal) {
        if (client == null || signal == null)
            throw new IllegalArgumentException("EReaderThread.start() arguments cannot be null");
        final EReaderThread that = new EReaderThread(client, signal);
        // Note: (and this is stupid, but that's the way TWS API works)
        // `EReader` must be constructed **after** `EClientSocket` is connected.
        that.ereader = new EReader(client, signal);
        that.ereader.start();
        that.ereaderThread.start();
        that.ereaderThread.setPriority(Thread.MAX_PRIORITY);
        that.ereaderThread.setName("EReader Thread");
        return that;
    }

    public synchronized void close() {
        ereaderThread.interrupt();
        stopThread(ereaderThread);
        if (ereader == null) {
            throw new IllegalStateException("EReader should have been initialized by now");
        }
        ereader.interrupt();
        stopThread(ereader);
    }

    private void processMessageQueue() {
        while (!Thread.interrupted()) {
            if (client.isConnected()) {
                signal.waitForSignal();
                try {
                    ereader.processMsgs();
                } catch (IOException e) {
                    logger.log(Logger.Level.ERROR, "TWS EReader error: " + e.getMessage());
                }
            } else {
                try {
                    Thread.sleep(WAIT_FOR_CONNECTION_TIMMEOUT);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public static void stopThread(final Thread thread) {
        try {
            thread.join(STOP_THREAD_TIMEOUT);
        } catch (InterruptedException e) {
            return;
        }
        if (thread.isAlive()) {
            logger.log(Logger.Level.WARNING,
                    MessageFormat.format("Thread named {0} did not stop in time", thread.getName()));
        }
    }
}
