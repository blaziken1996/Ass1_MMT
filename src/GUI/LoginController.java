package GUI;

/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class LoginController {

    Client client = null;
    @FXML
    private JFXTextField txtServerIP;
    @FXML
    private JFXTextField txtPort;
    @FXML
    private JFXTextField txtChatID;
    @FXML
    private JFXButton btnGo;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        String serverIP = txtServerIP.getText();
        String portNumber = txtPort.getText();
        Socket socket;
        int port;
        if (serverIP != null && portNumber != null) {
            try {
                port = Integer.parseInt(portNumber);
                socket = new Socket(serverIP, port);
                client = new Client(socket, txtChatID.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Port Number");
                alert.setHeaderText(null);
                alert.setContentText("Port number must be an integer");
                alert.showAndWait();
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientGUI.fxml"));
        Parent par = fxmlLoader.load();
        ClientGUI clientGUIController = fxmlLoader.getController();
        Scene Client = new Scene(par);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Client");
        stage.setScene(Client);
        clientGUIController.setClient(client);
        stage.show();
    }
}
