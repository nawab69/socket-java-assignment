import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private static ServerSocket server;
    private static int port = 3344;

    public static void main(String args[]) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            System.out.println("Waiting for the client request");
            Socket socket = server.accept();
            System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());

            // Create separate threads for reading and writing
            new Thread(() -> handleClientInput(socket)).start();
            new Thread(() -> handleClientOutput(socket)).start();
        }
    }

    private static void handleClientInput(Socket socket) {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                try {
                    String message = (String) ois.readObject();
                    System.out.println("Message Received from " + socket.getInetAddress().getHostAddress() + ": " + message);
                } catch (IOException | ClassNotFoundException e) {
                    // Handle client disconnection
                    System.out.println("Client disconnected: " + socket.getInetAddress().getHostAddress());
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientOutput(Socket socket) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                try {
                    String reply = reader.readLine();
                    oos.writeObject(reply);
                    System.out.println("Message sent to " + socket.getInetAddress().getHostAddress() + ": " + reply);
                } catch (IOException e) {
                    // Handle client disconnection
                    System.out.println("Client disconnected: " + socket.getInetAddress().getHostAddress());
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
