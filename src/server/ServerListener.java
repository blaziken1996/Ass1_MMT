package server;

import common.ClientSocket;
import common.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Created by trung on 16/09/2016.
 */

public class ServerListener extends Thread {

    private ServerClient client;
    private ConcurrentHashMap<InetSocketAddress, ServerClient> clientAddressMap;
    //private String clientAddress;

    public ServerListener(ServerClient socket, ConcurrentHashMap<InetSocketAddress, ServerClient> addressMap) {
        client = socket;
        clientAddressMap = addressMap;
    }

    @Override
    public void run() {
        try {
            InputStream input = client.getInputStream();
            //client.setName(Protocol.readString(input));
            //Get client ip address
            //clientAddress = client.getSocket().getRemoteSocketAddress().toString().substring(1);
            clientAddressMap.put(client.getAddress(), client);
            System.out.println("Connected with client at " + client.getAddress() + " " + client.getName());
            boolean isConnected = true;
            while (isConnected) {
                switch (Protocol.readInt(input)) {
                    case Protocol.ONLINE_LIST_CODE:
                        sendList();
                        break;
                    case Protocol.SEND_MSG_CODE:
                        sendMessage(Protocol.readInetAddress(input), Protocol.readString(input));
                        break;
                    case Protocol.FILE_REQ_CODE:
                        sendFileRequest(Protocol.readInetAddress(input), Protocol.readString(input));
                        break;
                    case Protocol.END_CONNECT_CODE:
                        isConnected = false;
                        client.write(asList(Protocol.intToBytes(Protocol.END_CONNECT_CODE)));
                        clientAddressMap.remove(client.getAddress());
                        break;
                    case Protocol.ACCEPT_FILE:
                        fileReqAccept(Protocol.readInetAddress(input), Protocol.readString(input),
                                Protocol.readInetAddress(input), Protocol.readInetAddress(input));
                        break;
                    case Protocol.DENY_FILE:
                        fileReqDenied(Protocol.readInetAddress(input));
                        break;
                }
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clientAddressMap.remove(client.getAddress());
            System.out.println("Client at " + client.getAddress() + " disconnect from server.");
        }
    }

    private void sendList() {
        /*List<String> stringList = new ArrayList<>(clientAddressMap.size() + 1);
        stringList.add(String.valueOf(clientAddressMap.size()));
        stringList.addAll(clientAddressMap.entrySet().stream().map(entry -> entry.getKey() + " " + entry.getValue().getName()).collect(Collectors.toList()));
        client.write(stringList, Protocol.ONLINE_LIST_CODE);*/
        try {
            List<byte[]> packet = new ArrayList<>(clientAddressMap.size() * 2 + 2);
            packet.add(Protocol.intToBytes(Protocol.ONLINE_LIST_CODE));
            packet.add(Protocol.intToBytes(clientAddressMap.size()));
            for (Map.Entry<InetSocketAddress, ServerClient> e : clientAddressMap.entrySet()) {
                packet.add(Protocol.inetAddressToBytes(e.getKey()));
                packet.add(Protocol.stringToBytes(e.getValue().getName()));
            }
            client.write(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fileReqDenied(InetSocketAddress fromAddress) {
        try {
            clientAddressMap.get(fromAddress).write(asList(Protocol.intToBytes(Protocol.DENY_FILE),
                    Protocol.stringToBytes(client.getName()), Protocol.inetAddressToBytes(client.getAddress())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFileRequest(InetSocketAddress receiverAddress, String filename) {
        ClientSocket receiver = clientAddressMap.get(receiverAddress);
        try {
            if (receiver != null) {
                receiver.write(asList(Protocol.intToBytes(Protocol.FILE_REQ_CODE)
                        , Protocol.stringToBytes(client.getName()), Protocol.inetAddressToBytes(client.getAddress()),
                        Protocol.stringToBytes(filename)));

            } else
                client.write(asList(Protocol.intToBytes(Protocol.NOT_AVAIL), Protocol.inetAddressToBytes(receiverAddress)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*clientAddressMap.get(receiver).write(asList(client.getName(),
                client.getSocket().getRemoteSocketAddress().toString(), filename), Protocol.FILE_REQ_CODE);*/
    }

    private void fileReqAccept(InetSocketAddress receiver, String name, InetSocketAddress address, InetSocketAddress server) {
        /*clientAddressMap.get(receiver).write(asList(name, address, server, port), Protocol.ACCEPT_FILE);*/
        try {
            /*byte[] nam = name.getBytes(Protocol.ENCODE);
            byte[] add = address.getBytes(Protocol.ENCODE);
            byte[] ser = server.getBytes(Protocol.ENCODE);*/

            clientAddressMap.get(receiver).write(asList(Protocol.intToBytes(Protocol.ACCEPT_FILE),
                    Protocol.stringToBytes(name), Protocol.inetAddressToBytes(address),
                    Protocol.inetAddressToBytes(server)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(InetSocketAddress toAddress, String message) {
        try {
            ClientSocket receiver = clientAddressMap.get(toAddress);
            if (receiver != null) {
                receiver.write(asList(Protocol.intToBytes(Protocol.SEND_MSG_CODE),
                        Protocol.stringToBytes(client.getName()), Protocol.inetAddressToBytes(client.getAddress()),
                        Protocol.stringToBytes(message)));
            } else {
                client.write(asList(Protocol.intToBytes(Protocol.NOT_AVAIL),Protocol.inetAddressToBytes(toAddress)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
