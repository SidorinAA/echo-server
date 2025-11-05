package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sidorin Aleksei
 * @version 1.0
 * @since 01.11.2025
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            MultithreadingTcpEchoServer server = new MultithreadingTcpEchoServer();
            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Received shutdown signal");
                try {
                    server.stop();
                } catch (InterruptedException e) {
                    log.error("Shutdown interrupted", e);
                    Thread.currentThread().interrupt();
                }
            }));
        } catch (InterruptedException e) {
            log.error("Server execution interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}