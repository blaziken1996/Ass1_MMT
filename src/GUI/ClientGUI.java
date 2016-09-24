package GUI;/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import common.DisplayOutput;
import common.Protocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
            client.write(null, Protocol.ONLINE_LIST_CODE);
        }
    }

    @Override
    public void show(String output) {
        onlineList.getItems().add(output);
    }
}
