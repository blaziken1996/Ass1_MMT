package GUI;

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import common.Protocol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Created by Dark Light on 26/09/2016.
 */
public class ChatWindowController implements Initializable {
    public static Client client;
    public static ConcurrentHashMap<String, ChatWindowController> chatWindows;

    @FXML
    private AnchorPane pane;
    @FXML
    private JFXTextArea txtChat;
    @FXML
    private JFXButton btnSend;
    @FXML
    private JFXTextArea txtEnter;
    @FXML
    private JFXListView<String> chatScreen;

    private String receiverAddress;

    public static ChatWindowController ChatWindowsCreate(String title, String address) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatWindowController.class.getResource("ChatWindow.fxml"));
        Parent par = fxmlLoader.load();
        Scene scene = new Scene(par);
        ChatWindowController chatWindowController = fxmlLoader.getController();
        chatWindowController.setReceiverAddress(address);
        chatWindowController.setPaneBinding(scene);
        chatWindows.put(address, chatWindowController);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            chatWindows.remove(address);
        });
        stage.show();
        return chatWindowController;
    }

    public JFXTextArea getTxtChat() {
        return txtChat;
    }

    private void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    private void setPaneBinding(Scene scene) {
        pane.prefHeightProperty().bind(scene.heightProperty());
        pane.prefWidthProperty().bind(scene.widthProperty());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSend.setOnAction(event -> {
            try {
                txtChat.appendText("Me: " + txtEnter.getText() + "\n");
                chatScreen.getItems().add("Me: " + txtEnter.getText() + "\n");
                client.write(asList(Protocol.intToBytes(Protocol.SEND_MSG_CODE),
                        Protocol.stringToBytes(receiverAddress), Protocol.stringToBytes(txtEnter.getText())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            txtEnter.clear();
        });
        chatScreen.setCellFactory(param -> new ListCell<String>() {
            {
                Text text = new Text();
                text.wrappingWidthProperty().bind(param.widthProperty().subtract(30));
                text.textProperty().bind(itemProperty());
                setPrefWidth(0);
                setGraphic(text);
                setAlignment(Pos.CENTER_LEFT);
            }
        });
    }

    public JFXListView<String> getChatScreen() {
        return chatScreen;
    }
}
