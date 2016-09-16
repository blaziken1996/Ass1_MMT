package server;

import common.Protocol;
import common.SocketWithDataFormat;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;

/**
 * Created by trung on 16/09/2016.
 */

interface GetFilePath {
    String getpath(String filename);
}

public class ServerListener extends Thread {
    ServerSocket serverSocket;
    HashMap<String, SocketWithDataFormat> socketHashMap;

    public ServerListener(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        socketHashMap = new HashMap<>();
    }

    public ServerListener(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendList(DataOutputStream output) throws IOException {
        Set<String> set = socketHashMap.keySet();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) output.writeUTF(iterator.next());
    }

    private void sendMessage(DataOutputStream output) {

    }

    @Override
    public void run() {
        while (true) {
            try {
                SocketWithDataFormat connected = new SocketWithDataFormat(serverSocket.accept());
                String remoteAddress = connected.getSocket().getRemoteSocketAddress().toString();
                socketHashMap.put(remoteAddress, connected);
                System.out.println("Connected with client at " + remoteAddress);
                DataInputStream input = connected.getInputStream();
                DataOutputStream output = connected.getOutputStream();
                boolean isConnected = true;
                while (isConnected) {
                    switch (input.readInt()) {
                        case Protocol.ONLINE_LIST:
                            sendList(output);
                            break;
                        case Protocol.SEND_FILE:
                            break;
                        case Protocol.INIT_CHAT:
                            break;
                        case Protocol.END_CHAT:
                            isConnected = false;
                            break;
                        case Protocol.SEND_MSG:
                            input.readUTF();
                            break;
                        case Protocol.FILE_RECEIVED:
                            break;
                    }
                }
                output.flush();
                connected.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

