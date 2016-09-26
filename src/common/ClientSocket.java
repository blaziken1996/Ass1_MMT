package common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by trung on 9/26/16.
 */
public abstract class ClientSocket {
    protected Socket socket;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected String name;
    protected String address;

    protected ClientSocket(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public String getAddress() {
        return address;
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
}
