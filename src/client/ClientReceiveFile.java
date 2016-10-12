package client;

import GUI.ChatWindowController;
import GUI.ChatMessage;
import common.Protocol;
import common.ReceiveFile;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by trung on 22/09/2016.
 */
public class ClientReceiveFile extends Thread {
    private InetSocketAddress socketAddress;
    private File saveFile;
    private Socket socketReceive;
    private ChatWindowController controller;

    public ClientReceiveFile(File filePath, String host, int port, ChatWindowController controller) {
        try {
            this.saveFile = filePath;
            this.controller = controller;
            socketReceive = new Socket(host, port);
            socketAddress = (InetSocketAddress) socketReceive.getLocalSocketAddress();
            socketReceive.getOutputStream().write(Protocol.intToBytes(Protocol.RECEIVE_FILE_SOCKET));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    @Override
    public void run() {
        try {
            ReceiveFile.receive(saveFile, socketReceive);
            socketReceive.close();
            Platform.runLater(() -> controller.showMessage("Finished sending file !") );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
