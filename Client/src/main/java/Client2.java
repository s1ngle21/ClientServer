import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class Client2 {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final String HOST = "localhost";
    private static final String EXIT = "-exit";
    private static final String FILE = "-file";
    private static final int PORT = 4567;


    public static void main(String[] args) {
        connectToServer();
    }

    public static void connectToServer() {
        try {
            Socket clientSocket = new Socket(HOST, PORT);

            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

            LOGGER.info("Connected to server");

            handleMessage(dis);

            handleCommand(dos);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleMessage(DataInputStream dis) {
        try {
            String message = dis.readUTF();
            LOGGER.info(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void handleCommand(DataOutputStream dos) throws IOException {
            LOGGER.info("Write a command");
            String command;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                command = reader.readLine();
                dos.writeUTF(command);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (command.startsWith(FILE)) {
                sendFile(dos, command.trim().split(" ")[1].trim());
            } else {
                LOGGER.info("Disconnected from server");
            }
    }

    private static void sendFile(DataOutputStream dos, String filePath) {
        File file = new File(filePath);

        try (FileInputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[1048576];
            int bytesReaded = is.read(buffer);
            if (bytesReaded != -1) {
                dos.write(buffer, 0, bytesReaded);
            }
            LOGGER.info("File has been send to server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
