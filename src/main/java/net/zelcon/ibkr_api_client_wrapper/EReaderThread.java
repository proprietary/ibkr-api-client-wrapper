// https://interactivebrokers.github.io/tws-api/connection.html#ereader

package net.zelcon.ibkr_api_client_wrapper;

import java.time.Duration;
import com.ib.client.*;
import java.io.IOException;
import java.lang.System.Logger;
import java.text.MessageFormat;

public class EReaderThread {
    private static final Duration WAIT_FOR_CONNECTION_TIMMEOUT = Duration.ofSeconds(5);

    private static final Duration STOP_THREAD_TIMEOUT = Duration.ofSeconds(5);

    private static final Logger logger = System.getLogger(EReaderThread.class.getName());

    private final EClientSocket client;
    private final EReaderSignal signal;
    private EReader ereader;
    private Thread ereaderThread = new Thread(this::processMessageQueue);

    private EReaderThread(final EClientSocket client, final EReaderSignal signal) {
        this.client = client;
        this.signal = signal;
        assert this.client != null;
        assert this.signal != null;
    }

    public static EReaderThread start(final EClientSocket client, final EReaderSignal signal) {
        final EReaderThread that = new EReaderThread(client, signal);
        // Note: (and this is stupid, but that's the way TWS API works)
        // `EReader` must be constructed **after** `EClientSocket` is connected.
        that.ereader = new EReader(client, signal);
        that.ereader.start();
        that.ereaderThread.start();
        that.ereaderThread.setPriority(Thread.MAX_PRIORITY);
        return that;
    }

    public synchronized void stop() {
        if (ereader == null) {
            throw new IllegalStateException("EReader should have been initialized by now");
        }
        ereaderThread.interrupt();
        stopThread(ereaderThread);
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
                    return;
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
