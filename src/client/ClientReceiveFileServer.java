package client;

import common.ReceiveFile;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientReceiveFileServer extends Thread {
    private InetSocketAddress serverAddress;
    private File saveFile;
    private ServerSocket server;

    public ClientReceiveFileServer(File filePath) {
        try {
            this.saveFile = filePath;
            server = new ServerSocket(0);
            serverAddress = (InetSocketAddress) server.getLocalSocketAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    @Override
    public void run() {
        try {
            Socket client = server.accept();
            ReceiveFile.receive(saveFile, client);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
