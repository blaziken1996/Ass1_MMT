package server;

import common.ClientSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by trung on 07/10/2016.
 */
public class ServerClient extends ClientSocket {
    ServerClient(Socket socket, String name) throws IOException {
        super(socket, name);
        address = (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    ServerClient(Socket socket) throws IOException {
        super(socket, null);
        address = (InetSocketAddress) socket.getRemoteSocketAddress();
    }
}
