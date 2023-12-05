import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {
    public static void main(String[] args) throws UnknownHostException, IOException {

       
        // Read server host from the console
        BufferedReader hostReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter server host(eg: 192.168.68.110) : ");
        
        String serverHost = hostReader.readLine();

        InetAddress host = InetAddress.getByName(serverHost);

        Socket socket = new Socket(host.getHostName(), 3344);

        System.out.println("Connected to server on port 3344");

        // Create separate threads for reading and writing
        new Thread(() -> handleServerInput(socket)).start();
        new Thread(() -> handleServerOutput(socket)).start();
    }

    private static void handleServerInput(Socket socket) {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                try {
                    String message = (String) ois.readObject();
                    System.out.println("Message from Server: " + message);
                } catch (IOException | ClassNotFoundException e) {
                    // Handle server disconnection
                    System.out.println("Server disconnected");
                    // Close the socket
                    socket.close();

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleServerOutput(Socket socket) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                try {
                    String msg = reader.readLine();
                    oos.writeObject(msg);
                    System.out.println("Message sent to server: " + msg);
                } catch (IOException e) {
                    // Handle server disconnection
                    System.out.println("Server disconnected");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
