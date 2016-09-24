package client;

import common.DisplayOutput;
import common.Protocol;
import common.ReadInput;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Arrays.asList;

/**
 * Created by trung on 21/09/2016.
 */
public class ClientReadSocketInput extends Thread {
    private InputStream in;
    private boolean isRunning;
    private Client client;
    private DisplayOutput displayOutput;
    private ReadInput readInput;

    public ClientReadSocketInput(Client client, DisplayOutput displayOutput, ReadInput readInput) {
        this.in = client.getInputStream();
        this.displayOutput = displayOutput;
        this.readInput = readInput;
        isRunning = false;
        this.client = client;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    @Override
    public void run() {
        isRunning = true;
        try {
            while (isRunning) {
                switch (Protocol.readInt(in)) {
                    case Protocol.ONLINE_LIST_CODE:
                        int num = Protocol.readInt(in);
                        displayOutput.show(String.valueOf(num));
                        for (int i = 0; i < num; i++) {
                            displayOutput.show(Protocol.readString(in));
                        }
                        break;
                    case Protocol.SEND_MSG_CODE:
                        displayOutput.show(Protocol.readString(in) + "(" + Protocol.readString(in) + "): " + Protocol.readString(in));
                        break;
                    case Protocol.FILE_REQ_CODE:
                        String name = Protocol.readString(in);
                        String address = Protocol.readString(in);
                        String filename = Protocol.readString(in);
                        displayOutput.show(name + "(" + address + "): want to send you " + filename
                                + ". Do you want to receive this file? (Y: Yes, N: No)");
                        boolean noAnswer = true;
                        while (noAnswer) {
                            String ans = readInput.read();
                            if (ans.compareTo("Y") == 0) {
                                noAnswer = false;
                                displayOutput.show("Input path to save file: ");
                                do {
                                    ans = readInput.read();
                                } while (ans.isEmpty());
                                ClientReceiveFileServer server = new ClientReceiveFileServer(displayOutput, ans);
                                server.start();
                                byte[] add = address.getBytes(Protocol.ENCODE);
                                byte[] clientname = client.getName().getBytes(Protocol.ENCODE);
                                byte[] clientadd = client.getAddress().getBytes(Protocol.ENCODE);
                                byte[] serveradd = server.getServerAddress().getBytes(Protocol.ENCODE);

                                /*List<String> messages = asList(address,client.getName(),
                                        client.getSocket().getLocalSocketAddress().toString(),
                                        server.getServerAddress(),String.valueOf(server.getPort()));*/
                                client.write(asList(Protocol.intToBytes(Protocol.ACCEPT_FILE),
                                        Protocol.intToBytes(add.length), add,
                                        Protocol.intToBytes(clientname.length), clientname,
                                        Protocol.intToBytes(clientadd.length), clientadd,
                                        Protocol.intToBytes(serveradd.length), serveradd, Protocol.intToBytes(server.getPort())));
                            } else if (ans.compareTo("N") == 0) {
                                displayOutput.show("You deny file");
                                noAnswer = false;
                                byte[] add = address.getBytes(Protocol.ENCODE);
                                client.write(asList(Protocol.intToBytes(Protocol.DENY_FILE),
                                        Protocol.intToBytes(add.length), add));
                            } else {
                                displayOutput.show("Do you want to receive this file? (Y: Yes, N: No)");
                            }
                        }
                        break;
                    case Protocol.ACCEPT_FILE:
                        name = Protocol.readString(in);
                        address = Protocol.readString(in);
                        String host = Protocol.readString(in);
                        int port = Protocol.readInt(in);
                        displayOutput.show(name + "(" + address + ") accepts your file. Sending file...");
                        new ClientSendFile(host, port, client.getReceiverFileMap().get(address), displayOutput).start();
                        client.getReceiverFileMap().remove(address);
                        break;
                    case Protocol.DENY_FILE:
                        name = Protocol.readString(in);
                        address = Protocol.readString(in);
                        displayOutput.show(name + "(" + address + ") denies your file.");
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
    }
}
