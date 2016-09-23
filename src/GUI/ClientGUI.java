package GUI;/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ClientGUI {
    @FXML
    private JFXListView onlineList;

    @FXML
    private Label lbl;

    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }
}
