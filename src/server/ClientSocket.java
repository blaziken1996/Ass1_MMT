package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Created by trung on 16/09/2016.
 */
public class ClientSocket {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String name;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public void close() throws IOException {
        outputStream.flush();
        socket.close();
    }

    public synchronized void write(List<String> messages, int code) {
        try {
            outputStream.writeInt(code);
            if (messages == null) return;
            for (String message : messages) outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
