package client;

import common.ReceiveFile;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

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
            serverAddress = new InetSocketAddress(readHostInetAddress(),server.getLocalPort());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private InetAddress readHostInetAddress() {
        try {
            Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (interfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = interfaceEnumeration.nextElement();
                Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses();
                while (addressEnumeration.hasMoreElements()) {
                    InetAddress address = addressEnumeration.nextElement();
                    if (!address.isLinkLocalAddress() && !address.isLoopbackAddress()
                            && address instanceof Inet4Address)
                        return address;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
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
