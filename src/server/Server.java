package server;

import common.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by trung on 17/09/2016.
 */
public class Server {
    private ServerSocket serverSocket;
    private InetAddress inetAddress;
    private ConcurrentHashMap<InetSocketAddress, ServerClient> clientMap;
    private ConcurrentHashMap<InetSocketAddress, ServerClient> sendFileMap;
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientMap = new ConcurrentHashMap<>();
        sendFileMap = new ConcurrentHashMap<>();
        inetAddress = Protocol.readHostInetAddress();
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
            while (true)
                new ServerListener(new ServerClient(server.serverSocket.accept()), server.clientMap, server.sendFileMap).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }


}
