package server;

import ServerGUI.TerminalController;
import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by trung on 17/09/2016.
 */
public class Server extends Thread {
    private ServerSocket serverSocket;
    private InetAddress inetAddress;
    private ConcurrentHashMap<InetSocketAddress, ServerClient> clientMap;
    private ConcurrentHashMap<InetSocketAddress, ServerClient> sendFileMap;
    private int port;
    private TerminalController controller;
    public Server(int port) throws IOException {
        this.port = port;
        clientMap = new ConcurrentHashMap<>();
        sendFileMap = new ConcurrentHashMap<>();
        inetAddress = readHostInetAddress();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                serverSocket = new ServerSocket(port);
                Platform.runLater(() -> controller.consoleLog("Server socket opens at " + getInetAddress() + " port " + serverSocket.getLocalPort()));
//                System.out.println("Server socket opens at " + getInetAddress() + " port " + serverSocket.getLocalPort());
                while (true)
                    new ServerListener(new ServerClient(serverSocket.accept()), clientMap, sendFileMap, controller).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static InetAddress readHostInetAddress() {
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

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() { return port; }

    public void setController(TerminalController controller) {
        this.controller = controller;
    }
}
