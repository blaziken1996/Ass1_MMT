package GUI;
/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import common.Protocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

public class ClientGUI implements Initializable {
    @FXML
    private JFXListView<String> onlineList;

    @FXML
    private Label lbl;

    @FXML
    private JFXButton btn;

    @FXML
    private JFXButton btnReset;


    private Client client;
    private ConcurrentHashMap<String, ChatWindowController> chatWindows;

    public JFXListView<String> getOnlineList() {
        return onlineList;
    }

    public void setClient(Client client) {
        ChatWindowController.client = this.client = client;
        lbl.setText(client.getName());
    }

    public ConcurrentHashMap<String, ChatWindowController> getChatWindows() {
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
                    String[] ss = s.split("\\s+");
                    try {
                        ChatWindowController.ChatWindowsCreate("Chat with " + ss[1] + "(" + ss[0] + ")", ss[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                return listCell;
            }
        });
    }

}
