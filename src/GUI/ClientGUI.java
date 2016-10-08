package GUI;
/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXButton;
import common.Protocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

public class ClientGUI implements Initializable {
    @FXML
    private ListView<String> onlineList;

    @FXML
    private Label lbl;

    @FXML
    private JFXButton btn;

    @FXML
    private JFXButton btnReset;


    private Client client;
    private ConcurrentHashMap<InetSocketAddress, ChatWindowController> chatWindows;
    private ConcurrentHashMap<InetSocketAddress, File> fileReceiver;

    public ListView<String> getOnlineList() {
        return onlineList;
    }

    public void setClient(Client client) {
        ChatWindowController.client = this.client = client;
        ChatWindowController.fileReceiver = fileReceiver = client.getReceiverFileMap();
        lbl.setText(client.getName());
    }

    public ConcurrentHashMap<InetSocketAddress, ChatWindowController> getChatWindows() {
        return chatWindows;
    }

    @FXML
    private void handleButtonAction(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnReset) {
            onlineList.getItems().clear();
        } else {
            try {
                client.write(asList(Protocol.intToBytes(Protocol.ONLINE_LIST_CODE)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void show(String output) {
        onlineList.getItems().add(output);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChatWindowController.chatWindows = chatWindows = new ConcurrentHashMap<>();
        onlineList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> listCell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item);
                    }
                };
                listCell.setOnMouseClicked(event -> {
                    String s = listCell.getText();
                    if (s == null) return;
                    String[] ss = s.split("[:\\s]+");
                    try {
                        ChatWindowController.ChatWindowsCreate("Chat with " + ss[2] + "/" + ss[0] + ":" + ss[1], new InetSocketAddress(ss[0], Integer.parseInt(ss[1])));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                return listCell;
            }
        });
    }

    public ConcurrentHashMap<InetSocketAddress, File> getFileReceiver() {
        return fileReceiver;
    }
}
