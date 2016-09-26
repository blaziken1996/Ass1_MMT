package GUI;/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import common.Protocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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

    public ConcurrentHashMap<String, ChatWindowController> getChatWindows() {
        return chatWindows;
    }

    public void setChatWindows(ConcurrentHashMap<String, ChatWindowController> chatWindows) {
        this.chatWindows = chatWindows;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    private void handleButtonAction(ActionEvent actionEvent) {
        lbl.setText(client.getName());
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

    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        String sumthing = onlineList.getSelectionModel().getSelectedItem();
        sumthing = sumthing.substring(0, sumthing.indexOf(" ") + 1);
        ChatWindowController chatWindowController = new ChatWindowController();
        chatWindows.put(sumthing, chatWindowController);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ChatWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Chat with " + sumthing);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
