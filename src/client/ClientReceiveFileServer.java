package client;

import common.DisplayOutput;
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
    private String filePath;
    private ServerSocket server;
    private DisplayOutput displayOutput;

    public ClientReceiveFileServer(DisplayOutput displayOutput, String filePath) {
        try {
            this.filePath = filePath;
            this.displayOutput = displayOutput;
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
            File file = new File(filePath);
            ReceiveFile.receive(file, client);
            client.close();
            displayOutput.show("Successfully received file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
