package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by trung on 16/09/2016.
 */
public class ClientSocket {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String name;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
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

    public InputStream getInputStream() {
        return inputStream;
    }

    public void close() throws IOException {
        outputStream.flush();
        socket.close();
    }

    public synchronized void write(List<byte[]> packet) throws IOException {

        for (byte[] b : packet) outputStream.write(b);
    }
    /*public synchronized void write(List<String> messages, int code) {
        try {
            outputStream.writeInt(code);
            if (messages == null) return;
            for (String message : messages) outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
