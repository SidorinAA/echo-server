package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Sidorin Aleksei
 * @version 1.0
 * @since 01.11.2025
 */
public class ClientHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final String clientId;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    /**
     * Основной метод обработки клиентского соединения выполняет следующие действия:
     * Считывает каждую строку входных данных.
     * Увеличивает счетчик обработанных сообщений.
     * Логирует полученное сообщение с уровнем DEBUG.
     * Отправляет обратно клиенту эхо-ответ с тем же содержимым.
     * При получении команды "STOP" (без учета регистра) завершает обработку.
     *
     * @implNote Метод выполняется в отдельном потоке для каждого клиентского соединения
     * @throws RuntimeException Не объявляет проверяемых исключений, но логирует все IOException
     */
    @Override
    public void run() {
        log.info("Client connected: {}, thread: {}", clientId, Thread.currentThread().getName());

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(
                     clientSocket.getOutputStream(), true)) {

            String inputLine;
            int messageCount = 0;

            while ((inputLine = in.readLine()) != null) {
                messageCount++;
                log.debug("Received from {} (message #{}): {}", clientId, messageCount, inputLine);

                out.println(inputLine);
                out.flush();
                log.debug("Echoed to {} (message #{}): {}", clientId, messageCount, inputLine);

                if ("STOP".equalsIgnoreCase(inputLine.trim())) {
                    log.info("Client {} requested disconnect after {} messages", clientId, messageCount);
                    break;
                }
            }

            log.info("Client {} disconnected. Total messages processed: {}", clientId, messageCount);

        } catch (IOException e) {
            log.error("Error handling client {}", clientId, e);
        } finally {
            try {
                clientSocket.close();
                log.debug("Client socket closed for: {}", clientId);
            } catch (IOException e) {
                log.error("Error closing client socket for {}", clientId, e);
            }
        }
    }
}
