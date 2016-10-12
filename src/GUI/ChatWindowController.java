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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;
import static javafx.scene.layout.AnchorPane.*;

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
    private ListView<ChatMessage> chatScreen;
    @FXML
    private JFXButton btnSendFile;
    @FXML
    private Label lblName;
    @FXML
    private Label lblAddress;

    private InetSocketAddress receiverAddress;
    private String receiverName;


    public static ChatWindowController ChatWindowsCreate(String name, InetSocketAddress address) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatWindowController.class.getResource("fxml/ChatWindow.fxml"));
        Parent par = fxmlLoader.load();
        Scene scene = new Scene(par);
        ChatWindowController chatWindowController = fxmlLoader.getController();
        chatWindowController.setReceiverAddress(address);
        chatWindowController.setLblNameAndAddress(name, address.toString());
        //chatWindowController.setPaneBinding(scene);
        chatWindows.put(address, chatWindowController);
        Stage stage = new Stage();
        stage.setMinWidth(500);
        stage.setMinHeight(330);
        stage.setTitle("Chat with " + name + " (" + address.toString().substring(1) +")");
        stage.getIcons().add(new Image("GUI/img/chat-2-icon.png"));
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> chatWindows.remove(address));
        stage.show();
        stage.toFront();
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
                chatScreen.getItems().add(new ChatMessage(txtEnter.getText(), true));
                client.write(asList(Protocol.intToBytes(Protocol.SEND_MSG_CODE),
                        Protocol.inetAddressToBytes(receiverAddress), Protocol.stringToBytes(txtEnter.getText())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            txtEnter.clear();
        });
        btnSend.setDefaultButton(true);
        pane.getStylesheets().add(getClass().getResource("css/btnFile.css").toExternalForm());
        pane.getStylesheets().add(getClass().getResource("css/listview.css").toExternalForm());
        chatScreen.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {
            @Override
            public ListCell<ChatMessage> call(ListView<ChatMessage> param) {
                ListCell<ChatMessage> cell = new ListCell<ChatMessage>() {
                    @Override
                    protected void updateItem(ChatMessage item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            Label label = new Label();
                            label.setText(item.getText());
                            label.setWrapText(true);
                            label.setPadding(new Insets(10));
                            setPrefWidth(0);
                            setGraphic(label);
                            if (item.isSelf()){
                                label.setTextAlignment(TextAlignment.RIGHT);
                                label.setAlignment(Pos.CENTER_RIGHT);
                                setAlignment(Pos.CENTER_RIGHT);
                                label.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white");
                            }
                            else {
                                setAlignment(Pos.CENTER_LEFT);
                                label.setStyle("-fx-background-color: #BBDEFB");
                            }

                        }
                        else setText("");
                    }
                };
                return cell;
            }
        });

        chatScreen.getItems().addListener(new ListChangeListener<ChatMessage>() {
            @Override
            public void onChanged(Change<? extends ChatMessage> c) {
                Platform.runLater(() -> chatScreen.scrollTo(chatScreen.getItems().size()-1));
            }
        });
    }

    public ListView<ChatMessage> getChatScreen() {
        return chatScreen;
    }

    public boolean showAcceptFilePopup(String fileName, String name, InetSocketAddress address) {
        Stage dialog = new Stage();
        HBox hBox = new HBox();
        JFXButton btnOK = new JFXButton();
        btnOK.setText("Accept");
        JFXButton btnCancel = new JFXButton();
        btnCancel.setText("Decline");
        btnOK.setPrefSize(60, 30);
        btnCancel.setPrefSize(60, 30);
        btnOK.setButtonType(JFXButton.ButtonType.RAISED);
        btnCancel.setButtonType(JFXButton.ButtonType.RAISED);
        btnOK.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white");
        btnCancel.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white");
        Text text = new Text("Do you want to accept file " + fileName + " from " + name + address + " ?");
        text.setWrappingWidth(250);
        text.setTextAlignment(TextAlignment.CENTER);
        text.prefHeight(63);
        hBox.getChildren().add(btnOK);
        hBox.getChildren().add(btnCancel);
        VBox vbox = new VBox();
        vbox.setLayoutX(48);
        vbox.setLayoutY(-44);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(377, 110);
        setBottomAnchor(vbox, 0.0);
        setTopAnchor(vbox, 0.0);
        setLeftAnchor(vbox, 0.0);
        setRightAnchor(vbox, 0.0);
        vbox.getChildren().addAll(text, hBox);
        text.setTextAlignment(TextAlignment.CENTER);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(20);
        anchorPane.getChildren().addAll(vbox);
        boolean[] result = {false};
        btnOK.setOnAction(event -> {
            result[0] = true;
            dialog.close();
        });

        btnCancel.setOnAction(event -> dialog.close());
        dialog.setScene(new Scene(anchorPane));
        dialog.initModality(Modality.APPLICATION_MODAL);
        //dialog.setResizable(false);
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
        Platform.runLater(() -> chatScreen.getItems().add(new ChatMessage(message)));
    }

    public void setLblNameAndAddress(String name, String address) {
        this.lblName.setText(name);
        this.lblAddress.setText(address.substring(1));
        receiverName = name;
    }

    public String getReceiverName() {
        return receiverName;
    }
}
