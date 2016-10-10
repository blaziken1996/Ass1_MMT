package GUI;

/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import client.ClientReadSocketInput;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import common.Protocol;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;

public class LoginController implements Initializable {

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
    private AnchorPane pane;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
//        String serverIP = txtServerIP.getText();
//        String portNumber = txtPort.getText();
        String serverIP = "localhost";
        String portNumber = "5000";
        Socket socket;
        int port;
        if (serverIP != null && portNumber != null) {
            try {
                port = Integer.parseInt(portNumber);
                socket = new Socket(serverIP, port);
                client = new Client(socket, txtChatID.getText());
                client.write(asList(Protocol.stringToBytes(client.getName())));
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
        Client.getStylesheets().add(getClass().getResource("listview.css").toExternalForm());
        stage.setScene(Client);
        clientGUIController.setClient(client);
        clientGUIController.getOnlineListFirstTime();
        client.setClientGUI(clientGUIController);
        new ClientReadSocketInput(client, clientGUIController).start();
        stage.setOnCloseRequest(e -> {
            try {
                client.write(asList(Protocol.intToBytes(Protocol.END_CONNECT_CODE)));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> pane.requestFocus());
    }
}
