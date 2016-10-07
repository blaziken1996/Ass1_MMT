package common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by trung on 9/26/16.
 */
public abstract class ClientSocket {
    protected InetSocketAddress address;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String name;

    protected ClientSocket(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public void close() throws IOException {
        outputStream.flush();
        socket.close();
    }

    public InetSocketAddress getAddress() {
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
