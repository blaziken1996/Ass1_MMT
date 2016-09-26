package GUI;/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import common.Protocol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.io.InputStream;
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
        this.client = client;
    }

    public ConcurrentHashMap<String, ChatWindowController> getChatWindows() {
        return chatWindows;
    }

    @FXML
    private void handleButtonAction(ActionEvent actionEvent) {
        lbl.setText(client.getName());
        if (actionEvent.getSource() == btnReset) {
            onlineList.getItems().clear();
        } else {
            try {
                client.write(asList(Protocol.intToBytes(Protocol.ONLINE_LIST_CODE)));
                Task<ObservableList<String>> readList = new Task<ObservableList<String>>() {
                    @Override
                    protected ObservableList<String> call() throws Exception {
                        InputStream in = client.getInputStream();
                        ObservableList<String> result = FXCollections.observableArrayList();
                        if (Protocol.readInt(in) == Protocol.ONLINE_LIST_CODE) {
                            int num = Protocol.readInt(in);
                            for (int i = 0; i < num; i++) {
                                result.add(Protocol.readString(in));
                            }
                        }
                        return result;
                    }
                };
                readList.setOnSucceeded(event -> onlineList.setItems(readList.getValue()));
                new Thread(readList).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void show(String output) {
        onlineList.getItems().add(output);
    }

    public void onMouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatWindows = new ConcurrentHashMap<>();
        onlineList.setCellFactory(param -> {
            JFXListCell<String> cell = new JFXListCell();
            cell.setOnMouseClicked(event -> {
                String sumthing = cell.getItem();
                sumthing = sumthing.substring(0, sumthing.indexOf(" ") + 1);
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
                    Parent par = fxmlLoader.load();
                    ChatWindowController chatWindowController = fxmlLoader.getController();
                    chatWindows.put(sumthing, chatWindowController);
                    Stage stage = new Stage();
                    stage.setTitle("Chat with " + sumthing);
                    stage.setScene(new Scene(par));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return cell;
        });
    }
}
