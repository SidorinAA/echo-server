package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sidorin Aleksei
 * @version 1.0
 * @since 04.11.2025
 */
@ExtendWith(MockitoExtension.class)
class ClientHandlerTest {

    @Test
    void run_shouldEchoMessagesCorrectly() throws IOException {
        String testInput = "Hello\nTest\nSTOP\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(inputStream);
        when(mockSocket.getOutputStream()).thenReturn(outputStream);
        when(mockSocket.getInetAddress()).thenReturn(java.net.InetAddress.getLocalHost());
        when(mockSocket.getPort()).thenReturn(12345);

        ClientHandler handler = new ClientHandler(mockSocket);

        handler.run();

        String result = outputStream.toString();
        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("Test"));
        assertTrue(result.contains("STOP"));
    }

    @Test
    void run_shouldProcessMultipleMessages() throws IOException {
        String[] messages = {"First", "Second", "Third", "STOP"};
        String testInput = String.join("\n", messages) + "\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream()).thenReturn(inputStream);
        when(mockSocket.getOutputStream()).thenReturn(outputStream);
        when(mockSocket.getInetAddress()).thenReturn(java.net.InetAddress.getLocalHost());
        when(mockSocket.getPort()).thenReturn(12345);

        ClientHandler handler = new ClientHandler(mockSocket);

        handler.run();

        String result = outputStream.toString();
        for (String message : messages) {
            assertTrue(result.contains(message), "Should contain: " + message);
        }
    }
}