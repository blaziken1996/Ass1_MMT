package client;

import common.DisplayOutput;
import common.Protocol;
import common.ReadInput;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by trung on 21/09/2016.
 */
public class ClientReadSocketInput extends Thread {
    private DataInputStream in;
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
                switch (in.readInt()) {
                    case Protocol.ONLINE_LIST_CODE:
                        int num = Integer.parseInt(in.readUTF());
                        for (int i = 0; i < num; i++) {
                            displayOutput.show(in.readUTF());
                        }
                        break;
                    case Protocol.SEND_MSG_CODE:
                        displayOutput.show(in.readUTF() + "(" + in.readUTF() + "): " + in.readUTF());
                        break;
                    case Protocol.FILE_REQ_CODE:
                        String name = in.readUTF();
                        String address = in.readUTF();
                        String filename = in.readUTF();
                        displayOutput.show(name + "(" + address + "): want to send you " + filename
                                + "Do you want to receive this file? (Y: Yes, N: No)");
                        boolean noAnswer = true;
                        while (noAnswer) {
                            String ans = readInput.read();
                            if (ans.compareTo("Y") == 0) {
                                noAnswer = false;
                                ArrayList<String> messages = new ArrayList<>(5);
                                messages.add(address);
                                messages.add(client.getName());
                                messages.add(client.getSocket().getLocalSocketAddress().toString());
                                ClientReceiveFileServer server = new ClientReceiveFileServer(displayOutput);
                                server.start();
                                messages.add(server.getServerAddress());
                                messages.add(String.valueOf(server.getPort()));
                                client.write(messages, Protocol.ACCEPT_FILE);
                            } else if (ans.compareTo("N") == 0) {
                                displayOutput.show("You deny file");
                                noAnswer = false;
                                ArrayList<String> messages = new ArrayList<>(1);
                                messages.add(address);
                                client.write(messages, Protocol.DENY_FILE);
                            } else {
                                displayOutput.show("Do you want to receive this file? (Y: Yes, N: No)");
                            }
                        }
                        break;
                    case Protocol.ACCEPT_FILE:
                        name = in.readUTF();
                        address = in.readUTF();
                        String host = in.readUTF();
                        int port = Integer.parseInt(in.readUTF());
                        displayOutput.show(name + "(" + address + ") accepts your file. Sending file...");
                        new ClientSendFile(host, port, client.getReceiverFileMap().get(address), displayOutput).start();
                        client.getReceiverFileMap().remove(address);
                        break;
                    case Protocol.DENY_FILE:
                        name = in.readUTF();
                        address = in.readUTF();
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
