package client;

import common.DisplayOutput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientReceiveFileServer extends Thread {
    private String serverAddress;
    private int port;
    private String FilePath = "receivefile";
    private ServerSocket server;
    private DisplayOutput displayOutput;

    public ClientReceiveFileServer(DisplayOutput displayOutput) {
        try {
            this.displayOutput = displayOutput;
            server = new ServerSocket(2222);
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

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    @Override
    public void run() {
        try {
            Socket client = server.accept();
            File file = new File(FilePath);
            FileOutputStream out = new FileOutputStream(file);
            InputStream in = client.getInputStream();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) > 0) out.write(buffer, 0, count);
            out.flush();
            out.close();
            client.close();
            displayOutput.show("Successfully received file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
