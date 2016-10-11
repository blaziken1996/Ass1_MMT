package client;

import GUI.ChatWindowController;
import GUI.ClientGUI;
import common.Protocol;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Created by trung on 21/09/2016.
 */
public class ClientReadSocketInput extends Thread {
    private InputStream in;
    private Client client;
    private ClientGUI clientGUI;
    private ConcurrentHashMap<InetSocketAddress, ChatWindowController> chatWindows;

    public ClientReadSocketInput(Client client, ClientGUI clientGUI) {
        this.in = client.getInputStream();
        this.clientGUI = clientGUI;
        this.chatWindows = clientGUI.getChatWindows();
        this.client = client;
    }

    private void responseFileRequest(ChatWindowController controller, String filename, String name, InetSocketAddress address) throws IOException {
        boolean confirm = controller.showAcceptFilePopup(filename, name, address);
        if (confirm) {
            File saveFile = controller.saveFileLocation(filename);
            if (saveFile != null) {
                ClientReceiveFile receiveFile = new ClientReceiveFile(saveFile, client.getHost(), client.getPort());
                receiveFile.start();

                client.write(asList(Protocol.intToBytes(Protocol.ACCEPT_FILE),
                        Protocol.inetAddressToBytes(address),
                        Protocol.stringToBytes(client.getName()),
                        Protocol.inetAddressToBytes(client.getAddress()),
                        Protocol.inetAddressToBytes(receiveFile.getSocketAddress())));

            } else client.write(asList(Protocol.intToBytes(Protocol.DENY_FILE),
                    Protocol.inetAddressToBytes(address)));
        } else {
                client.write(asList(Protocol.intToBytes(Protocol.DENY_FILE),
                        Protocol.inetAddressToBytes(address)));
        }
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
                            list.add(Protocol.readInetAddress(in).toString().substring(1) + " " + Protocol.readString(in));
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
                        InetSocketAddress address = Protocol.readInetAddress(in);
                        String message = Protocol.readString(in);
                        ChatWindowController chat = chatWindows.get(address);
                        if (chat == null) {
                            task = new Task<Void>() {
                                @Override
                                protected Void call() {
                                    Platform.runLater(() -> {
                                        ChatWindowController controller = null;
                                        try {
                                            controller = ChatWindowController.ChatWindowsCreate(name, address);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        controller.showMessage(name +": " + message);
                                    });
                                    return null;
                                }
                            };
                            new Thread(task).start();
                        } else {
                            new Thread(new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    chat.showMessage(name + ": " + message);
                                    return null;
                                }
                            }).start();
                        }
                        //clientGUI.show(Protocol.readString(in) + "(" + Protocol.readString(in) + "): " + Protocol.readString(in));
                        break;
                    case Protocol.FILE_REQ_CODE:
                        name = Protocol.readString(in);
                        address = Protocol.readInetAddress(in);
                        String filename = Protocol.readString(in);
                        ChatWindowController controller = chatWindows.get(address);
                        if (controller == null) {
                            new Thread(new Task<Void>() {
                                @Override
                                protected Void call() {
                                    Platform.runLater(() -> {
                                        ChatWindowController controller = null;
                                        try {
                                            controller = ChatWindowController.ChatWindowsCreate(name, address);
                                            responseFileRequest(controller, filename, name, address);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    return null;
                                }
                            }).start();
                        } else {
                            new Thread(new Task<Void>() {
                                @Override
                                protected Void call() {
                                    Platform.runLater(() -> {
                                        try {
                                            responseFileRequest(controller, filename, name, address);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    return null;
                                }
                            }).start();
                        }
                        break;
                    case Protocol.ACCEPT_FILE:
                        name = Protocol.readString(in);
                        address = Protocol.readInetAddress(in);
                        InetSocketAddress receiveAddress = Protocol.readInetAddress(in);
                        System.out.println("From readsocket: " + client.getReceiverFileMap().get(address) + " " + address);
                        chatWindows.get(address).showMessage(name + " has accepted your request. Sending file...");
                        new ClientSendFile(client.getHost(), client.getPort(), client.getReceiverFileMap().get(address), receiveAddress).start();
                        client.getReceiverFileMap().remove(address);
                        break;
                    case Protocol.DENY_FILE:
                        name = Protocol.readString(in);
                        address = Protocol.readInetAddress(in);
                        chatWindows.get(address).showMessage(name + " has denied your request.");
                        client.getReceiverFileMap().remove(address);
                        break;
                    case Protocol.NOT_AVAIL:
                        chatWindows.get(address = Protocol.readInetAddress(in)).showMessage("This person is offline.");
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
