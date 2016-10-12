package GUI;
/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXButton;
import common.Protocol;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import javafx.util.Duration;

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
    private Label lblName;
    @FXML
    private Label lblAddress;
    @FXML
    private Label lblPort;
    @FXML
    private JFXButton btnUpdate;

    @FXML
    private ImageView imgView;

    private Client client;
    private ConcurrentHashMap<InetSocketAddress, ChatWindowController> chatWindows;
    private ConcurrentHashMap<InetSocketAddress, File> fileReceiver;
    private Image onlineIcon;

    public ListView<String> getOnlineList() {
        return onlineList;
    }

    public void setClient(Client client) {
        ChatWindowController.client = this.client = client;
        ChatWindowController.fileReceiver = fileReceiver = client.getReceiverFileMap();
        lblName.setText(client.getName());
        lblAddress.setText("IP: " + client.getAddress().toString().substring(1, client.getAddress().toString().indexOf(":") ));
        lblPort.setText("Port: " + String.valueOf(client.getPort()));
    }

    public ConcurrentHashMap<InetSocketAddress, ChatWindowController> getChatWindows() {
        return chatWindows;
    }

    @FXML
    private void getOnlineList(ActionEvent actionEvent) {
        onlineList.getItems().clear();
        try {
            client.write(asList(Protocol.intToBytes(Protocol.ONLINE_LIST_CODE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void show(String output) {
        onlineList.getItems().add(output);
    }

    private void getOnlineListAutomatic() {
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            getOnlineList(event);
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ChatWindowController.chatWindows = chatWindows = new ConcurrentHashMap<>();
        onlineIcon = new Image(getClass().getResourceAsStream("img/online.png"));
        onlineList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> listCell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setGraphic(new ImageView(onlineIcon));
                            setText(item);
                        }
                    }
                };
                listCell.setOnMouseClicked(event -> {
                    String s = listCell.getText();
                    if (s == null) return;
                    String[] ss = s.split("[(:)]+");
                    try {
                        ChatWindowController.ChatWindowsCreate(ss[0], new InetSocketAddress(ss[1], Integer.parseInt(ss[2])));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onlineList.getSelectionModel().clearSelection();
                });

                return listCell;
            }
        });
        getOnlineListAutomatic();
    }

    public void getOnlineListFirstTime() {
        try {
            client.write(asList(Protocol.intToBytes(Protocol.ONLINE_LIST_CODE)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConcurrentHashMap<InetSocketAddress, File> getFileReceiver() {
        return fileReceiver;
    }
}
