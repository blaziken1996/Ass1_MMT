package GUI;

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import common.Protocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Created by Dark Light on 26/09/2016.
 */
public class ChatWindowController implements Initializable {
    public static Client client;
    public static ConcurrentHashMap<InetSocketAddress, ChatWindowController> chatWindows;
    public static ConcurrentHashMap<InetSocketAddress, File> fileReceiver;

    @FXML
    private AnchorPane pane;
    @FXML
    private JFXButton btnSend;
    @FXML
    private JFXTextArea txtEnter;
    @FXML
    private JFXListView<String> chatScreen;
    @FXML
    private JFXButton btnSendFile;
    private InetSocketAddress receiverAddress;


    public static ChatWindowController ChatWindowsCreate(String title, InetSocketAddress address) throws IOException {
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

    private void setReceiverAddress(InetSocketAddress receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    private void setPaneBinding(Scene scene) {
        pane.prefHeightProperty().bind(scene.heightProperty());
        pane.prefWidthProperty().bind(scene.widthProperty());
    }

    @FXML
    private void locateFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose file to send");
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            System.out.println(receiverAddress);
            System.out.println(file);
            fileReceiver.put(receiverAddress, file);
            try {
                client.write(asList(Protocol.intToBytes(Protocol.FILE_REQ_CODE), Protocol.inetAddressToBytes(receiverAddress),
                        Protocol.stringToBytes(file.getName())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSend.setOnAction(event -> {
            try {
                chatScreen.getItems().add("Me: " + txtEnter.getText());
                client.write(asList(Protocol.intToBytes(Protocol.SEND_MSG_CODE),
                        Protocol.inetAddressToBytes(receiverAddress), Protocol.stringToBytes(txtEnter.getText())));
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

    public boolean showAcceptFilePopup(String fileName, String name, InetSocketAddress address) {
//        JFXPopup popup = new JFXPopup();
//        popup.setPrefSize(150, 300);
//        HBox hBox = new HBox();
//        JFXButton btnOK = new JFXButton();
//        btnOK.setText("OK");
//        JFXButton btnCancel =  new JFXButton();
//        btnCancel.setText("Cancel");
//        Text text = new Text("Do you want to accept file " + fileName + " from " + name + "(" + address +") ?");
//        hBox.getChildren().add(btnOK);
//        hBox.getChildren().add(btnCancel);
//        VBox vbox = new VBox();
//        AnchorPane pane = new AnchorPane();
//        vbox.getChildren().addAll(text, hBox);
//        pane.getChildren().addAll(vbox);
//        popup.setPopupContainer(pane);
//        final boolean[] result = new boolean[1];
//        result[0] = false;
//        btnOK.setOnAction(event -> {
//            result[0] = true;
//            popup.close();
//        });
//        btnCancel.setOnAction(event -> popup.close() );
//        popup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
//        return  result[0];
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to accept file " + fileName + " from " + name + address + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    public File saveFileLocation(String filename) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose where to save file");
        chooser.setInitialFileName(filename);
        File file = chooser.showSaveDialog(new Stage());
        return file;
    }

    public void showMessage(String message) {
        chatScreen.getItems().add(message);
    }

}
