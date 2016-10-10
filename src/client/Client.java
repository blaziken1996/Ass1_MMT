package client;

import GUI.ClientGUI;
import common.ClientSocket;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by trung on 16/09/2016.
 */
public class Client extends ClientSocket {
    private ConcurrentHashMap<InetSocketAddress, File> receiverFileMap;
    private ClientGUI clientGUI;
    private String host;
    private int port;

    public Client(String host, int port, String name) throws IOException {
        super(new Socket(host, port), name);
        this.host = host;
        this.port = port;
        address = (InetSocketAddress) socket.getLocalSocketAddress();
        receiverFileMap = new ConcurrentHashMap<>();
    }

    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    public ConcurrentHashMap<InetSocketAddress, File> getReceiverFileMap() {
        return receiverFileMap;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
