package client;

import ClientGUI.ChatWindowController;
import common.Protocol;
import common.SendFile;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientSendFile extends Thread {
    private Socket client;
    private File file;
    private ChatWindowController controller;

    public ClientSendFile(String host, int port, File file, InetSocketAddress address, ChatWindowController controller) {
        try {
            client = new Socket(host, port);
            this.controller = controller;
            client.getOutputStream().write(Protocol.intToBytes(Protocol.SEND_FILE_SOCKET));
            client.getOutputStream().write(Protocol.inetAddressToBytes(address));
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
            Platform.runLater(() -> controller.showMessage("Finished sending file !") );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
