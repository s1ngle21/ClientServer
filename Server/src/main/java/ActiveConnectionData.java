import java.net.Socket;

public class ActiveConnectionData {
    private String name;
    private long timeOfConnection;
    private Socket clientSocket;

    public ActiveConnectionData(String name, long timeOfConnection, Socket clientSocket) {
        this.name = name;
        this.timeOfConnection = timeOfConnection;
        this.clientSocket = clientSocket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeOfConnection() {
        return timeOfConnection;
    }

    public void setTimeOfConnection(long timeOfConnection) {
        this.timeOfConnection = timeOfConnection;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}
