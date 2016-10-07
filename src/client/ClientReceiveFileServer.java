package client;

import common.ReceiveFile;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientReceiveFileServer extends Thread {
    private String serverAddress;
    private int port;
    private File saveFile;
    private ServerSocket server;

    public ClientReceiveFileServer(File filePath) {
        try {
            this.saveFile = filePath;
            server = new ServerSocket(0);
            serverAddress = InetAddress.getLocalHost().getHostAddress();
            port = server.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getPort() {
        return port;
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
