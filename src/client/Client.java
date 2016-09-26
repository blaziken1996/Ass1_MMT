package client;

import GUI.ClientGUI;
import common.ClientSocket;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by trung on 16/09/2016.
 */
public class Client extends ClientSocket {
    private ConcurrentHashMap<String, File> receiverFileMap;
    private ClientGUI clientGUI;

    public Client(Socket socket, String name) throws IOException {
        super(socket, name);
        this.address = socket.getLocalSocketAddress().toString().substring(1);
        receiverFileMap = new ConcurrentHashMap<>();
    }

    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    public ConcurrentHashMap<String, File> getReceiverFileMap() {
        return receiverFileMap;
    }
}
