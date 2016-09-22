package GUI;

/**
 * Created by Dark Light on 22/09/2016.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private JFXTextField txtServerIP;

    @FXML
    private JFXTextField txtPort;

    @FXML
    private JFXTextField txtChatID;

    @FXML
    private JFXButton btnGo;

    @FXML
    void handleButtonAction(ActionEvent event) throws IOException {
        String serverIp = txtServerIP.getText();
        Parent par = FXMLLoader.load(getClass().getResource("ClientGUI.fxml"));
        Scene Client = new Scene(par);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Client");
        stage.setScene(Client);
        stage.show();
    }
}
