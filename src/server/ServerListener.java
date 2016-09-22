package server;

import common.Protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by trung on 16/09/2016.
 */

public class ServerListener extends Thread {

    private ClientSocket client;
    private ConcurrentHashMap<String, ClientSocket> clientAddressMap;

    public ServerListener(ClientSocket socket, ConcurrentHashMap<String, ClientSocket> addressmap) {
        client = socket;
        clientAddressMap = addressmap;
    }

    private void sendList() {
        Iterator<Map.Entry<String, ClientSocket>> iter = clientAddressMap.entrySet().iterator();
        List<String> stringList = new ArrayList<>(clientAddressMap.size() + 1);
        stringList.add(String.valueOf(clientAddressMap.size()));
        while (iter.hasNext()) {
            Map.Entry<String, ClientSocket> entry = iter.next();
            stringList.add(entry.getKey() + " " + entry.getValue().getName());
        }
        client.write(stringList, Protocol.ONLINE_LIST_CODE);
    }

    @Override
    public void run() {
        try {
            DataInputStream input = client.getInputStream();
            client.setName(input.readUTF());
            //Get client ip address
            String clientAddress = client.getSocket().getRemoteSocketAddress().toString();
            clientAddressMap.put(clientAddress, client);
            System.out.println("Connected with client at " + clientAddress + " " + client.getName());
            boolean isConnected = true;
            while (isConnected) {
                switch (input.readInt()) {
                    case Protocol.ONLINE_LIST_CODE:
                        sendList();
                        break;
                    case Protocol.SEND_MSG_CODE:
                        sendMessage(input.readUTF(), input.readUTF());
                        break;
                    case Protocol.FILE_REQ_CODE:
                        sendFileRequest(input.readUTF(), input.readUTF());
                        break;
                    case Protocol.END_CONNECT_CODE:
                        isConnected = false;
                        client.write(null, Protocol.END_CONNECT_CODE);
                        clientAddressMap.remove(clientAddress);
                        break;
                    case Protocol.ACCEPT_FILE:
                        fileReqAccept(input.readUTF(), input.readUTF(), input.readUTF(), input.readUTF(), input.readUTF());
                        break;
                    case Protocol.DENY_FILE:
                        fileReqDenied(input.readUTF());
                        break;
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            String clientAddress = client.getSocket().getRemoteSocketAddress().toString();
            clientAddressMap.remove(clientAddress);
            System.out.println("Client at " + clientAddress + " disconnect from server.");
        }
    }

    private void fileReqDenied(String fromAddress) {
        ArrayList<String> messages = new ArrayList<>(2);
        messages.add(client.getName());
        messages.add(client.getSocket().getRemoteSocketAddress().toString());
        clientAddressMap.get(fromAddress).write(messages, Protocol.DENY_FILE);
    }

    private void sendFileRequest(String receiver, String filename) {
        ArrayList<String> messages = new ArrayList<>(2);
        messages.add(client.getName());
        messages.add(client.getSocket().getRemoteSocketAddress().toString());
        messages.add(filename);
        clientAddressMap.get(receiver).write(messages, Protocol.FILE_REQ_CODE);
    }

    private void fileReqAccept(String receiver, String name, String address, String server, String port) {
        ArrayList<String> messages = new ArrayList<>(2);
        messages.add(name);
        messages.add(address);
        messages.add(server);
        messages.add(port);
        clientAddressMap.get(receiver).write(messages, Protocol.ACCEPT_FILE);
    }

    private boolean sendMessage(String toAddress, String message) {
        ArrayList<String> messages = new ArrayList<>(3);
        messages.add(client.getName());
        messages.add(client.getSocket().getRemoteSocketAddress().toString());
        messages.add(message);
        ClientSocket receiver = clientAddressMap.get(toAddress);
        if (receiver != null) {
            receiver.write(messages, Protocol.SEND_MSG_CODE);
            return true;
        } else return false;
    }

}
