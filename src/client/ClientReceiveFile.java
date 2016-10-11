package client;

import common.Protocol;
import common.ReceiveFile;

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

    public ClientReceiveFile(File filePath, String host, int port) {
        try {
            this.saveFile = filePath;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
