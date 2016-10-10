package GUI;

import client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import common.Protocol;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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
    private ListView<String> chatScreen;
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
        stage.setMinWidth(495);
        stage.setMinHeight(330);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> chatWindows.remove(address));
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
    private void chooseFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose file to send");
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
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
        txtEnter.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Event parentEvent = event.copyFor(pane, pane);
                pane.fireEvent(parentEvent);
                event.consume();
            }
        });
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
        btnSend.setDefaultButton(true);
        pane.getStylesheets().add(getClass().getResource("btnFile.css").toExternalForm());
        pane.getStylesheets().add(getClass().getResource("listview.css").toExternalForm());
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
        chatScreen.getItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                Platform.runLater(() -> chatScreen.scrollTo(chatScreen.getItems().size()-1));
            }
        });
    }

    public ListView<String> getChatScreen() {
        return chatScreen;
    }

    public boolean showAcceptFilePopup(String fileName, String name, InetSocketAddress address) {
        Stage dialog = new Stage();
        HBox hBox = new HBox();
        JFXButton btnOK = new JFXButton();
        btnOK.setText("OK");
        JFXButton btnCancel = new JFXButton();
        btnCancel.setText("Cancel");
        Text text = new Text("Do you want to accept file " + fileName + " from " + name + address + " ?");
        hBox.getChildren().add(btnOK);
        hBox.getChildren().add(btnCancel);
        VBox vbox = new VBox();
        AnchorPane anchorPane = new AnchorPane();
        vbox.getChildren().addAll(text, hBox);
        text.setTextAlignment(TextAlignment.CENTER);
        hBox.setAlignment(Pos.CENTER);
        anchorPane.getChildren().addAll(vbox);
        boolean[] result = {false};
        btnOK.setOnAction(event -> {
            result[0] = true;
            dialog.close();
        });

        btnCancel.setOnAction(event -> dialog.close());
        dialog.setScene(new Scene(anchorPane));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
        return result[0];
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
