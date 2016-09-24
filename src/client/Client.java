package client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by trung on 16/09/2016.
 */
public class Client {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ConcurrentHashMap<String, File> receiverFileMap;
    private String name;
    private String address;

    public Client(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        this.address = socket.getLocalSocketAddress().toString().substring(1);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        receiverFileMap = new ConcurrentHashMap<>();
    }

    public String getAddress() {
        return address;
    }

    public ConcurrentHashMap<String, File> getReceiverFileMap() {
        return receiverFileMap;
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public synchronized void write(List<byte[]> packet) throws IOException {

        for (byte[] b : packet) outputStream.write(b);
    }

    /*public synchronized void write(List<String> messages, int code) {
        try {
            if(code > 0) outputStream.writeInt(code);
            if (messages == null) return;
            for (String message : messages) outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

}
