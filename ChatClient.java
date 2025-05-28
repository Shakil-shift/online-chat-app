import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * ChatClient
 *  - Connects to the ChatServer at localhost:12345.
 *  - Reads user input and sends it to the server.
 *  - Listens for incoming broadcasts and displays them.
 */
public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        System.out.println("Connecting to chat server at " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            // Thread to listen for messages from server
            Thread listener = new Thread(() -> {
                String serverMsg;
                try {
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.err.println("Connection closed by server.");
                }
            });
            listener.start();

            // Main loop: read user input and send to server
            while (true) {
                System.out.print("> ");
                String msg = scanner.nextLine();
                if (msg.equalsIgnoreCase("/quit")) {
                    out.println("/quit");
                    System.out.println("You have left the chat.");
                    break;
                }
                out.println(msg);
            }
        } catch (IOException e) {
            System.err.println("Unable to connect to server: " + e.getMessage());
        }
    }
}