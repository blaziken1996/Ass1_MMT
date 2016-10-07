package client;

import common.SendFile;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientSendFile extends Thread {
    private Socket client;
    private File file;

    public ClientSendFile(InetAddress host, int port, File file) {
        try {
            client = new Socket(host, port);
            this.file = file;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            SendFile.send(file, client);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
