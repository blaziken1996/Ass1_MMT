package server;

import common.ClientSocket;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by trung on 16/09/2016.
 */
public class ServerClient extends ClientSocket {

    public ServerClient(Socket socket, String name) throws IOException {
        super(socket, name);
        address = socket.getRemoteSocketAddress().toString().substring(1);
    }

    public void close() throws IOException {
        outputStream.flush();
        socket.close();
    }

}
