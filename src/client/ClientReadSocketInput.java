package client;

import GUI.ChatWindowController;
import GUI.ClientGUI;
import common.Protocol;
import common.ReadInput;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Created by trung on 21/09/2016.
 */
public class ClientReadSocketInput extends Thread {
    private InputStream in;
    private Client client;
    private ReadInput readInput;
    private ClientGUI clientGUI;
    private ConcurrentHashMap<String, ChatWindowController> chatWindows;

    public ClientReadSocketInput(Client client, ClientGUI clientGUI) {
        this.in = client.getInputStream();
        this.clientGUI = clientGUI;
        this.chatWindows = clientGUI.getChatWindows();
        this.client = client;
    }

    @Override
    public void run() {
        boolean isRunning = true;
        try {
            while (isRunning) {
                switch (Protocol.readInt(in)) {
                    case Protocol.ONLINE_LIST_CODE:
                        ObservableList<String> list = FXCollections.observableArrayList();
                        int num = Protocol.readInt(in);
                        for (int i = 0; i < num; i++) {
                            list.add(Protocol.readString(in));
                        }
                        Task<Void> task = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                Platform.runLater(() -> {
                                    clientGUI.getOnlineList().setItems(list);
                                });
                                return null;
                            }
                        };
                        new Thread(task).start();
                        break;
                    case Protocol.SEND_MSG_CODE:
                        String name = Protocol.readString(in);
                        String address = Protocol.readString(in);
                        String message = Protocol.readString(in);
                        ChatWindowController chat = chatWindows.get(address);
                        if (chat == null) {
                            task = new Task<Void>() {
                                @Override
                                protected Void call() {
                                    Platform.runLater(() -> {
                                        ChatWindowController controller = null;
                                        try {
                                            controller = ChatWindowController.ChatWindowsCreate("Chat with " + name + "(" + address + ")", address);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        controller.getTxtChat().appendText(name + "(" + address + "): " + message + "\n");
                                        controller.getChatScreen().getItems().add(name + "(" + address + "): " + message + "\n");
                                    });
                                    return null;
                                }
                            };
                            new Thread(task).start();
                        } else {
                            task = new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    Platform.runLater(() -> {
                                        chat.getTxtChat().appendText(name + "(" + address + "): " + message + "\n");
                                        chat.getChatScreen().getItems().add(name + "(" + address + "): " + message + "\n");
                                    });
                                    return null;
                                }
                            };
                            new Thread(task).start();
                        }
                        //clientGUI.show(Protocol.readString(in) + "(" + Protocol.readString(in) + "): " + Protocol.readString(in));
                        break;
                    case Protocol.FILE_REQ_CODE:
                        name = Protocol.readString(in);
                        address = Protocol.readString(in);
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
    }
}
