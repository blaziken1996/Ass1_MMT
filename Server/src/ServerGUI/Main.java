package ServerGUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import server.Server;

import java.io.IOException;

/**
 * Created by Dark Light on 13/10/2016.
 */
public class Main extends Application {
    @FXML
    private Pane inputPane;

    @FXML
    private JFXButton btnSetup;

    @FXML
    private Pane resultPane;

    @FXML
    private JFXTextField txtPort;

    @FXML
    private AnchorPane anchorPane;

    private Server server;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    void serverSetup(ActionEvent event) {
        if (txtPort.getText() != null) {
            try {
                int port = Integer.parseInt(txtPort.getText());
                server = new Server(port);

                if (server != null) {
                    System.out.println("Sever setup completed");
                    server.start();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("terminal.fxml"));
                    Parent par = fxmlLoader.load();
                    Scene terminal = new Scene(par);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(terminal);
                    TerminalController controller = fxmlLoader.getController();
                    server.setController(controller);
                    controller.setInfo(server.getInetAddress().toString(), server.getPort());

                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Port Number");
                alert.setHeaderText(null);
                alert.setContentText("Port number must be an integer");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("What's up ? - Server");
        primaryStage.getIcons().add(new Image("ServerGUI/icon.png"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event ->{
            Platform.exit();
            System.exit(0);
        } );
    }
}
