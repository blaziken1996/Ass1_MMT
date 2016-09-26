package client;

import GUI.ClientGUI;
import common.Protocol;
import common.ReadInput;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Arrays.asList;

/**
 * Created by trung on 21/09/2016.
 */
public class ClientReadSocketInput extends Task<Void> {
    private InputStream in;
    private Client client;
    private ReadInput readInput;
    private ClientGUI clientGUI;

    public ClientReadSocketInput(Client client, ClientGUI clientGUI, ReadInput readInput) {
        this.in = client.getInputStream();
        this.clientGUI = clientGUI;
        this.readInput = readInput;
        this.client = client;
    }

    @Override
    public Void call() {
        boolean isRunning = true;
        try {
            while (isRunning) {
                switch (Protocol.readInt(in)) {
                    case Protocol.ONLINE_LIST_CODE:
                        int num = Protocol.readInt(in);
                        clientGUI.show(String.valueOf(num));
                        for (int i = 0; i < num; i++) {
                            clientGUI.show(Protocol.readString(in));
                        }
                        break;
                    case Protocol.SEND_MSG_CODE:
                        clientGUI.show(Protocol.readString(in) + "(" + Protocol.readString(in) + "): " + Protocol.readString(in));
                        break;
                    case Protocol.FILE_REQ_CODE:
                        String name = Protocol.readString(in);
                        String address = Protocol.readString(in);
                        String filename = Protocol.readString(in);
                        clientGUI.show(name + "(" + address + "): want to send you " + filename
                                + ". Do you want to receive this file? (Y: Yes, N: No)");
                        boolean noAnswer = true;
                        while (noAnswer) {
                            String ans = readInput.read();
                            if (ans.compareTo("Y") == 0) {
                                noAnswer = false;
                                clientGUI.show("Input path to save file: ");
                                do {
                                    ans = readInput.read();
                                } while (ans.isEmpty());
                                ClientReceiveFileServer server = new ClientReceiveFileServer(clientGUI, ans);
                                server.start();
                                byte[] add = address.getBytes(Protocol.ENCODE);
                                byte[] clientname = client.getName().getBytes(Protocol.ENCODE);
                                byte[] clientadd = client.getAddress().getBytes(Protocol.ENCODE);
                                byte[] serveradd = server.getServerAddress().getBytes(Protocol.ENCODE);

                                client.write(asList(Protocol.intToBytes(Protocol.ACCEPT_FILE),
                                        Protocol.intToBytes(add.length), add,
                                        Protocol.intToBytes(clientname.length), clientname,
                                        Protocol.intToBytes(clientadd.length), clientadd,
                                        Protocol.intToBytes(serveradd.length), serveradd, Protocol.intToBytes(server.getPort())));
                            } else if (ans.compareTo("N") == 0) {
                                clientGUI.show("You deny file");
                                noAnswer = false;
                                byte[] add = address.getBytes(Protocol.ENCODE);
                                client.write(asList(Protocol.intToBytes(Protocol.DENY_FILE),
                                        Protocol.intToBytes(add.length), add));
                            } else {
                                clientGUI.show("Do you want to receive this file? (Y: Yes, N: No)");
                            }
                        }
                        break;
                    case Protocol.ACCEPT_FILE:
                        name = Protocol.readString(in);
                        address = Protocol.readString(in);
                        String host = Protocol.readString(in);
                        int port = Protocol.readInt(in);
                        clientGUI.show(name + "(" + address + ") accepts your file. Sending file...");
                        new ClientSendFile(host, port, client.getReceiverFileMap().get(address), clientGUI).start();
                        client.getReceiverFileMap().remove(address);
                        break;
                    case Protocol.DENY_FILE:
                        name = Protocol.readString(in);
                        address = Protocol.readString(in);
                        clientGUI.show(name + "(" + address + ") denies your file.");
                        client.getReceiverFileMap().remove(address);
                        break;
                    case Protocol.END_CONNECT_CODE:
                        isRunning = false;
                        break;
                }
            }
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
