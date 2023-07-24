import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Server {

    static final int PORT = 4567;
    static final String HOST = "localhost";
    static final String CLIENT_NAME_TEMPLATE = "Client-%d";
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final String EXIT = "-exit";
    private static final String FILE = "-file";
    private static int clientCounter = 1;
    private static final List<ActiveConnectionData> ACTIVE_CONNECTIONS = new ArrayList<>();

    public static void start() {
        try (ServerSocket ss = new ServerSocket(PORT)) {
            LOGGER.info("Server started");
            while (true) {
                Socket clientSocket = ss.accept();
                connectClient(clientSocket);
                executeClientsCommand(clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void connectClient(Socket clientSocket) {
        ActiveConnectionData activeConnectionData = new ActiveConnectionData();
        String clientName = createClientName();
        long timeOfConnection = System.currentTimeMillis();
        activeConnectionData.setName(clientName);
        activeConnectionData.setTimeOfConnection(timeOfConnection);
        activeConnectionData.setClientSocket(clientSocket);
        ACTIVE_CONNECTIONS.add(activeConnectionData);
        LOGGER.info(clientName + " has been connected successfully. Time of connection is: " + timeOfConnection + "ms");
    }


    public static void executeClientsCommand(Socket clientSocket) {
        try {
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            String command = dis.readUTF();
            if ((command.trim().equals(EXIT))) {
                stopConnectionWithClient(clientSocket);
            }
            if ((command.trim().startsWith(FILE))) {
                receiveFile(clientSocket, command.trim().split(" ")[1].trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopConnectionWithClient(Socket clientSocket)  {
        LOGGER.info(getClientName(clientSocket) + " was disconnected");
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ACTIVE_CONNECTIONS.removeIf(activeConnectionData -> activeConnectionData.getClientSocket() == clientSocket);

    }


    public static void receiveFile(Socket clientSocket, String filePath) {
        try {
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            byte[] buffer = new byte[1048576];
            int bytesReaded = dis.read(buffer);
            while (bytesReaded != -1) {
                writer.write(new String(buffer, 0, bytesReaded));
            }
            writer.close();
            LOGGER.info("File received from " + getClientName(clientSocket));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String createClientName() {
        return String.format(CLIENT_NAME_TEMPLATE, clientCounter++);
    }

    public static String getClientName(Socket clientSocket) {
        for (ActiveConnectionData activeConnectionData : ACTIVE_CONNECTIONS) {
            if (activeConnectionData.getClientSocket() == clientSocket) {
                return activeConnectionData.getName();
            }
        }
        return null;
    }
}
