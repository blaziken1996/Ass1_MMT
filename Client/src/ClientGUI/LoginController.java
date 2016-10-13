package ClientGUI;

/**
 * Created by Dark Light on 22/09/2016.
 */

import client.Client;
import client.ClientReadSocketInput;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import common.Protocol;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
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
    private Label lblProgram;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        String serverIP = txtServerIP.getText();
        String portNumber = txtPort.getText();
//        String serverIP = "localhost";
//        String portNumber = "5000";
        Socket socket;
        int port;
        if (serverIP != null && portNumber != null) {
            try {
                port = Integer.parseInt(portNumber);
                client = new Client(serverIP, port, txtChatID.getText());
                client.write(asList(Protocol.intToBytes(Protocol.CHAT_SOCKET), Protocol.stringToBytes(client.getName())));
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/ClientGUI.fxml"));
                Parent par = fxmlLoader.load();
                ClientGUI clientGUIController = fxmlLoader.getController();
                Scene Client = new Scene(par);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("What's up ?");
                Client.getStylesheets().add(getClass().getResource("css/listview.css").toExternalForm());
                stage.setScene(Client);
                clientGUIController.setClient(client);
                client.setClientGUI(clientGUIController);
                clientGUIController.getOnlineListFirstTime();
                new ClientReadSocketInput(client, clientGUIController).start();
                stage.setOnCloseRequest(e -> {
                    try {
                        client.write(asList(Protocol.intToBytes(Protocol.END_CONNECT_CODE)));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
                stage.show();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Port Number");
                alert.setHeaderText(null);
                alert.setContentText("Port number must be an integer");
                alert.showAndWait();
            } catch (UnknownHostException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Unknown Server Address");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid server IP address.");
                alert.showAndWait();
            } catch (ConnectException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Connection Problem");
                alert.setHeaderText(null);
                alert.setContentText("Cannot establish connection to the given IP address and port. Please make sure the IP address and port number is of a running server.");
                alert.showAndWait();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblProgram.setStyle("-fx-font: bold 20pt Arial; -fx-text-fill: #757575");
        Platform.runLater(() -> pane.requestFocus());
    }
}
