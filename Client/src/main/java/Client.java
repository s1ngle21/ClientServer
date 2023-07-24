import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;


public class Client implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final String HOST = "localhost";
    private static final String EXIT = "-exit";
    private static final String FILE = "-file";
    private static final int PORT = 4567;


    @Override
    public void run() {
        connectToServer();
    }

    public static void connectToServer() {
        try (Socket clientSocket = new Socket(HOST, PORT);
             var out = new DataOutputStream(clientSocket.getOutputStream())) {
            handleCommand(out);
            out.write(Files.readAllBytes(Paths.get("test.txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleCommand(DataOutputStream out) throws IOException {
        LOGGER.info("Write a command");
        String command;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            command = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (command.startsWith(FILE)) {
            sendFile(out, command.trim().split(" ")[1].trim());
        } else {
            out.writeUTF(command);
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
