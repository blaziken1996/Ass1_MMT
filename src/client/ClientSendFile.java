package client;

import GUI.ClientGUI;
import common.SendFile;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientSendFile extends Thread {
    private Socket client;
    private File file;
    private ClientGUI clientGUI;

    public ClientSendFile(String host, int port, File file, ClientGUI displayOutput) {
        try {
            this.clientGUI = displayOutput;
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
            clientGUI.show("Successfully sent file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
