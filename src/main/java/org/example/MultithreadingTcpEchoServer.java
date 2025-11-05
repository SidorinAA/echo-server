package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Sidorin Aleksei
 * @version 1.0
 * @since 01.11.2025
 */
public class MultithreadingTcpEchoServer {

    private static final Logger log = LoggerFactory.getLogger(MultithreadingTcpEchoServer.class);
    private final int port;
    private final ExecutorService threadPool;
    private volatile boolean isRunning;
    private ServerSocket serverSocket;

    public MultithreadingTcpEchoServer(int port) {
        this.port = port;
        this.threadPool = Executors.newCachedThreadPool();
        this.isRunning = true;
        log.info("MultithreadingTcpEchoServer initialized with port {}", port);
    }

    public MultithreadingTcpEchoServer() {
        this(7);
    }

    public void  start() throws InterruptedException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            log.info("Echo Server started on port {}", port);
            log.info("Waiting for connections...");

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    threadPool.execute(clientHandler);

                } catch (SocketException e) {
                    if (isRunning) {
                        log.error("Socket error while accepting connection", e);
                    } else {
                        log.debug("Socket closed normally during shutdown");
                    }
                }
            }
        } catch (IOException e) {
            log.error("Server error during startup or operation", e);
        } finally {
            stop();
        }
    }

    public void stop() throws InterruptedException {
        log.info("Initiating server shutdown...");
        isRunning = false;

        threadPool.shutdown();

        if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
            log.warn("Forcing thread pool shutdown");
            threadPool.shutdownNow();
        }

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                log.debug("Server socket closed successfully");
            } catch (IOException e) {
                log.error("Error closing server socket", e);
            }
        }

        log.info("Echo Server stopped");
    }
}
