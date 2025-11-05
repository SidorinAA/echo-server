package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Sidorin Aleksei
 * @version 1.0
 * @since 04.11.2025
 */
class MultithreadingTcpEchoServerTest {

    private MultithreadingTcpEchoServer server;
    private static final int TEST_PORT = 12345;

    @BeforeEach
    void setUp() {
        server = new MultithreadingTcpEchoServer(TEST_PORT);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    @Timeout(5)
    void testStartServer() throws InterruptedException {
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        serverThread.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(serverThread.isAlive());

        server.stop();
        serverThread.interrupt();
    }

    @Test
    @Timeout(5)
    void testStopWhenAlreadyStopped() throws InterruptedException {
        server.stop();
        assertDoesNotThrow(() -> server.stop());
    }

    @Test
    @Timeout(5)
    void testServerSocketCreationFailure() {
        MultithreadingTcpEchoServer failingServer = new MultithreadingTcpEchoServer(-1);

        assertThrows(IllegalArgumentException.class, () -> failingServer.start());
    }
}