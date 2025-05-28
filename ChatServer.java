import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * ChatServer
 *  - Listens for client connections on port 12345.
 *  - Assigns a unique ID to each client.
 *  - Broadcasts incoming messages to all connected clients.
 */
public class ChatServer {
    private static final int PORT = 12345;
    // Thread-safe set to track all connected clients
    private static final Set<ClientHandler> clientHandlers =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void main(String[] args) {
        System.out.println("Chat server starting on port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            int nextUserId = 1;
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String userId = "User" + nextUserId++;
                ClientHandler handler = new ClientHandler(clientSocket, userId);
                clientHandlers.add(handler);
                new Thread(handler).start();
                System.out.println(userId + " connected.");
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message The message to send.
     * @param sender  The handler of the client who sent the message.
     */
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler handler : clientHandlers) {
            handler.sendMessage(sender.getUserId() + ": " + message);
        }
    }

    /**
     * Removes a client handler (e.g., when the client disconnects).
     *
     * @param handler The handler to remove.
     */
    public static void removeClient(ClientHandler handler) {
        boolean removed = clientHandlers.remove(handler);
        if (removed) {
            System.out.println(handler.getUserId() + " disconnected.");
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private String userId;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Welcome " + userId + "! You can start typing messages. Type /quit to exit.");
        } catch (IOException e) {
            System.err.println("I/O error for " + userId + ": " + e.getMessage());
            cleanup();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                ChatServer.broadcast(message, this);
            }
        } catch (IOException e) {
            // Client disconnected unexpectedly
        } finally {
            cleanup();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUserId() {
        return userId;
    }

    private void cleanup() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) { }
        ChatServer.removeClient(this);
    }
}