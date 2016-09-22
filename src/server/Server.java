package server;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by trung on 17/09/2016.
 */
public class Server {
    private ServerSocket serverSocket;
    private InetAddress inetAddress;
    private ConcurrentHashMap<String, ClientSocket> clientMap;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientMap = new ConcurrentHashMap<>();
        inetAddress = readHostInetAddress();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please specify a port");
            return;
        }
        int port = Integer.parseInt(args[0]);
        try {
            Server server = new Server(port);
            System.out.println("Server socket opens at " + server.getInetAddress() + " port " + port);
            while (true) new ServerListener(new ClientSocket(server.serverSocket.accept()), server.clientMap).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getInetAddress() {
        return inetAddress;
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
}
