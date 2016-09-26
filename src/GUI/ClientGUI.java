package GUI;/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import common.DisplayOutput;
import common.Protocol;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Arrays.asList;

public class ClientGUI implements DisplayOutput {
    @FXML
    private JFXListView<String> onlineList;

    @FXML
    private Label lbl;

    @FXML
    private JFXButton btn;

    @FXML
    private JFXButton btnReset;


    private Client client;


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
                readList.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        onlineList.setItems(readList.getValue());
                    }
                });
                new Thread(readList).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void show(String output) {
        onlineList.getItems().add(output);
    }
}
